#!/usr/bin/env bash

set -Eeuo pipefail

readonly READYROAD_ROOT="${READYROAD_ROOT:-/opt/readyroad}"
readonly READYROAD_RELEASES_DIR="${READYROAD_RELEASES_DIR:-${READYROAD_ROOT}/releases}"
readonly READYROAD_CURRENT_LINK="${READYROAD_CURRENT_LINK:-${READYROAD_ROOT}/current}"
readonly READYROAD_STATE_DIR="${READYROAD_STATE_DIR:-${READYROAD_ROOT}/rollback-state}"
readonly READYROAD_LOG_DIR="${READYROAD_LOG_DIR:-/var/log/readyroad-deploy}"
readonly READYROAD_NETWORK="${READYROAD_NETWORK:-readyroad-network}"

rr_log() {
  local level="$1"
  local event="$2"
  shift 2
  local timestamp line

  timestamp="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  line="timestamp=${timestamp} level=${level} event=${event} $*"
  if [[ -n "${READYROAD_DEPLOY_LOG:-}" ]]; then
    printf '%s\n' "$line" | tee -a "$READYROAD_DEPLOY_LOG"
  else
    printf '%s\n' "$line"
  fi
  logger --tag readyroad-deploy -- "$line"
}

rr_die() {
  rr_log CRITICAL deployment_error "message=$1"
  return 1
}

rr_require_commands() {
  local command
  for command in "$@"; do
    command -v "$command" >/dev/null 2>&1 ||
      rr_die "missing_required_command:${command}"
  done
}

rr_validate_release_id() {
  local value="$1"
  [[ "$value" =~ ^[A-Za-z0-9][A-Za-z0-9._-]{0,79}$ ]] ||
    rr_die "invalid_release_id"
}

rr_validate_git_ref() {
  local value="$1"
  [[ "$value" =~ ^[A-Za-z0-9][A-Za-z0-9._/-]{0,199}$ ]] ||
    rr_die "invalid_git_ref"
  [[ "$value" != *".."* && "$value" != *"@{"* ]] ||
    rr_die "unsafe_git_ref"
}

rr_manifest_value() {
  local key="$1"
  local manifest="$2"

  awk -F= -v key="$key" '$1 == key {sub(/^[^=]*=/, ""); value=$0} END {print value}' \
    "$manifest"
}

rr_require_sha256() {
  local value="$1"
  [[ "$value" =~ ^sha256:[0-9a-f]{64}$ ]]
}

rr_verify_image() {
  local image="$1"
  local expected_id="$2"
  local actual_id

  rr_require_sha256 "$expected_id" ||
    rr_die "invalid_image_id:${image}"
  actual_id="$(docker image inspect --format '{{.Id}}' "$image")"
  [[ "$actual_id" == "$expected_id" ]] ||
    rr_die "image_identity_mismatch:${image}"
  rr_log INFO image_verified "image=${image} image_id=${actual_id}"
}

rr_release_path() {
  local candidate="$1"
  local resolved

  resolved="$(readlink -f "$candidate")"
  [[ -n "$resolved" && -d "$resolved" ]] ||
    rr_die "release_not_found"
  case "$resolved" in
    "${READYROAD_RELEASES_DIR}/"*) printf '%s' "$resolved" ;;
    *) rr_die "release_outside_release_root" ;;
  esac
}

rr_validate_release_files() {
  local release="$1"
  local required

  for required in docker-compose.yml Caddyfile .env.production \
    release-manifest.env; do
    [[ -s "${release}/${required}" ]] ||
      rr_die "missing_release_file:${required}"
  done
}

rr_set_env_value() {
  local file="$1"
  local key="$2"
  local value="$3"
  local temporary="${file}.tmp.$$"

  awk -v key="$key" -v value="$value" '
    BEGIN {found=0}
    index($0, key "=") == 1 {
      print key "=" value
      found=1
      next
    }
    {print}
    END {
      if (!found) {
        print key "=" value
      }
    }
  ' "$file" >"$temporary"
  chmod --reference="$file" "$temporary"
  mv -f "$temporary" "$file"
}

rr_atomic_current_link() {
  local release="$1"
  local temporary="${READYROAD_ROOT}/.current.$$.tmp"

  ln -s "$release" "$temporary"
  mv -Tf "$temporary" "$READYROAD_CURRENT_LINK"
}

rr_wait_container_health() {
  local container="$1"
  local timeout_seconds="$2"
  local started status running

  started="$(date +%s)"
  while (( $(date +%s) - started < timeout_seconds )); do
    running="$(docker inspect --format '{{.State.Running}}' "$container" 2>/dev/null || true)"
    status="$(docker inspect --format \
      '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' \
      "$container" 2>/dev/null || true)"
    if [[ "$running" == "true" && "$status" == "healthy" ]]; then
      rr_log INFO container_healthy "container=${container}"
      return 0
    fi
    if [[ "$running" == "false" ]]; then
      rr_log ERROR container_stopped "container=${container}"
      return 1
    fi
    sleep 5
  done
  rr_log ERROR container_health_timeout \
    "container=${container} timeout_seconds=${timeout_seconds}"
  return 1
}

rr_wait_exec_http() {
  local container="$1"
  local url="$2"
  local timeout_seconds="$3"
  local started

  started="$(date +%s)"
  while (( $(date +%s) - started < timeout_seconds )); do
    if docker exec "$container" curl --fail --silent --max-time 5 "$url" \
      >/dev/null 2>&1; then
      rr_log INFO candidate_healthy "container=${container}"
      return 0
    fi
    if [[ "$(docker inspect --format '{{.State.Running}}' "$container" \
      2>/dev/null || true)" == "false" ]]; then
      rr_log ERROR candidate_stopped "container=${container}"
      return 1
    fi
    sleep 3
  done
  rr_log ERROR candidate_health_timeout "container=${container}"
  return 1
}

rr_compose() {
  local release="$1"
  shift
  local manifest="${release}/release-manifest.env"
  local backend_image frontend_image

  backend_image="$(rr_manifest_value BACKEND_IMAGE "$manifest")"
  frontend_image="$(rr_manifest_value FRONTEND_IMAGE "$manifest")"
  export BACKEND_IMAGE_TAG="${backend_image#readyroad-backend:}"
  export FRONTEND_IMAGE_TAG="${frontend_image#readyroad-frontend:}"

  docker compose \
    --project-directory "$release" \
    --env-file "${release}/.env.production" \
    --file "${release}/docker-compose.yml" \
    "$@"
}

rr_backend_runtime_fingerprint() {
  local release="$1"

  # Build paths and release tags do not change the running Spring application.
  rr_compose "$release" config --format json |
    jq -ceS '.services.backend | objects |
      del(.build, .image, .environment.BACKEND_IMAGE_TAG,
          .environment.FRONTEND_IMAGE_TAG)' |
    sha256sum | awk '{print $1}'
}

rr_activate_application() {
  local current_release="$1"
  local next_release="$2"
  local expected_image running_state current_fingerprint next_fingerprint

  expected_image="$(rr_manifest_value BACKEND_IMAGE_ID \
    "${next_release}/release-manifest.env")"
  running_state="$(docker inspect --format \
    '{{.Image}} {{.State.Running}} {{if .State.Health}}{{.State.Health.Status}}{{end}}' \
    readyroad-backend)"
  rr_require_sha256 "$expected_image" || rr_die "invalid_backend_image_id"
  current_fingerprint="$(rr_backend_runtime_fingerprint "$current_release")"
  next_fingerprint="$(rr_backend_runtime_fingerprint "$next_release")"

  if [[ "$running_state" == "${expected_image} true healthy" &&
        "$current_fingerprint" == "$next_fingerprint" ]]; then
    rr_log INFO backend_reused "reason=unchanged_image_and_runtime_config"
    rr_compose "$next_release" up -d --no-deps --no-build frontend
  else
    rr_compose "$next_release" up -d --no-build backend frontend
  fi
}

rr_is_valid_release() {
  local manifest="$1"
  local status

  [[ -s "$manifest" ]] || return 1
  status="$(rr_manifest_value DEPLOYMENT_STATUS "$manifest")"
  [[ -z "$status" || "$status" == "SUCCESS" ]]
}

rr_prune_releases() {
  local mode="$1"
  local keep="$2"
  local current_release="$3"
  local previous_release="$4"
  local release manifest
  local valid_position=0
  local -a releases=()

  [[ "$mode" == "dry-run" || "$mode" == "execute" ]] ||
    rr_die "invalid_cleanup_mode"
  [[ "$keep" =~ ^[2-9][0-9]*$|^[2-9]$ ]] ||
    rr_die "invalid_release_retention"

  mapfile -t releases < <(
    find "$READYROAD_RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d \
      -printf '%f\n' | sort -r
  )

  for release in "${releases[@]}"; do
    release="${READYROAD_RELEASES_DIR}/${release}"
    manifest="${release}/release-manifest.env"
    if ! rr_is_valid_release "$manifest"; then
      rr_log INFO release_cleanup_skip \
        "mode=${mode} reason=not_valid release=$(basename "$release")"
      continue
    fi

    valid_position=$((valid_position + 1))
    if (( valid_position <= keep )) ||
      [[ "$release" == "$current_release" || "$release" == "$previous_release" ]]; then
      rr_log INFO release_cleanup_keep \
        "mode=${mode} release=$(basename "$release") position=${valid_position}"
      continue
    fi

    rr_log INFO release_cleanup_delete \
      "mode=${mode} release=$(basename "$release") position=${valid_position}"
    if [[ "$mode" == "execute" ]]; then
      case "$release" in
        "${READYROAD_RELEASES_DIR}/"*)
          rm -rf --one-file-system -- "$release"
          ;;
        *)
          rr_die "unsafe_release_cleanup_path"
          ;;
      esac
    fi
  done
}

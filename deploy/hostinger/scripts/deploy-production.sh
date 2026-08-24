#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
if [[ -r /opt/readyroad/bin/readyroad-deploy-lib ]]; then
  # shellcheck source=/dev/null
  source /opt/readyroad/bin/readyroad-deploy-lib
  readonly TEMPLATE_DIR="/opt/readyroad/bin/templates"
  readonly SMOKE_SCRIPT="/opt/readyroad/bin/readyroad-smoke"
  readonly ROLLBACK_SCRIPT="/opt/readyroad/bin/readyroad-rollback"
  readonly BACKUP_SCRIPT="/usr/local/sbin/readyroad-backup-config"
else
  # shellcheck source=deploy-lib.sh
  source "${SCRIPT_DIR}/deploy-lib.sh"
  readonly TEMPLATE_DIR="${SCRIPT_DIR}/.."
  readonly SMOKE_SCRIPT="${SCRIPT_DIR}/production-smoke.sh"
  readonly ROLLBACK_SCRIPT="${SCRIPT_DIR}/rollback-release.sh"
  readonly BACKUP_SCRIPT="/usr/local/sbin/readyroad-backup-config"
fi

readonly BACKEND_REPOSITORY="https://github.com/haydartarek/readyroad.git"
readonly FRONTEND_REPOSITORY="https://github.com/haydartarek/readyroad_front_end.git"
BACKEND_REF="feature/postgresql-supabase"
FRONTEND_REF="feature/postgresql-supabase"
RELEASE_ID=""
RELEASE_RETENTION=5
DRY_RUN=0
SIMULATE_HEALTH_FAILURE=0
staging_directory=""
final_release=""
rollback_target=""
candidate_frontend=""
activation_started=0
backend_image=""
frontend_image=""
started_epoch="$(date +%s)"

usage() {
  cat >&2 <<'EOF'
Usage: readyroad-deploy [options]
  --backend-ref REF
  --frontend-ref REF
  --release-id ID
  --retain COUNT
  --dry-run
  --simulate-health-failure
EOF
  exit 2
}

while (( $# > 0 )); do
  case "$1" in
    --backend-ref)
      [[ $# -ge 2 ]] || usage
      BACKEND_REF="$2"
      shift 2
      ;;
    --frontend-ref)
      [[ $# -ge 2 ]] || usage
      FRONTEND_REF="$2"
      shift 2
      ;;
    --release-id)
      [[ $# -ge 2 ]] || usage
      RELEASE_ID="$2"
      shift 2
      ;;
    --retain)
      [[ $# -ge 2 ]] || usage
      RELEASE_RETENTION="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=1
      shift
      ;;
    --simulate-health-failure)
      SIMULATE_HEALTH_FAILURE=1
      shift
      ;;
    *)
      usage
      ;;
  esac
done

[[ "$EUID" -eq 0 ]] || {
  printf 'readyroad-deploy must run as root.\n' >&2
  exit 1
}
rr_validate_git_ref "$BACKEND_REF"
rr_validate_git_ref "$FRONTEND_REF"
[[ "$RELEASE_RETENTION" =~ ^[2-9][0-9]*$|^[2-9]$ ]] ||
  rr_die "invalid_release_retention"
if [[ -n "$RELEASE_ID" ]]; then
  rr_validate_release_id "$RELEASE_ID"
fi

install -d -o root -g adm -m 0750 "$READYROAD_LOG_DIR"
run_name="${RELEASE_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
READYROAD_DEPLOY_LOG="${READYROAD_LOG_DIR}/deploy-${run_name}.log"
export READYROAD_DEPLOY_LOG
touch "$READYROAD_DEPLOY_LOG"
chown root:adm "$READYROAD_DEPLOY_LOG"
chmod 0640 "$READYROAD_DEPLOY_LOG"

exec 9>/run/lock/readyroad-deploy.lock
if ! flock -n 9; then
  rr_die "deployment_already_running"
fi

rr_require_commands docker git curl jq flock grep sha256sum readlink
[[ -x "$SMOKE_SCRIPT" ]] || rr_die "smoke_script_missing"
[[ -x "$ROLLBACK_SCRIPT" ]] || rr_die "rollback_script_missing"
[[ -s "${TEMPLATE_DIR}/docker-compose.yml" ]] ||
  rr_die "compose_template_missing"
[[ -s "${TEMPLATE_DIR}/Caddyfile.production" ]] ||
  rr_die "caddy_template_missing"

cleanup_candidate_container() {
  if [[ -n "$candidate_frontend" ]]; then
    docker rm -f "$candidate_frontend" >/dev/null 2>&1 || true
    candidate_frontend=""
  fi
}

cleanup_staging() {
  if [[ -n "$staging_directory" &&
    "$staging_directory" == "${READYROAD_RELEASES_DIR}/.staging."* ]]; then
    if [[ -f "${staging_directory}/.env.production" ]]; then
      shred --remove "${staging_directory}/.env.production" 2>/dev/null ||
        rm -f "${staging_directory}/.env.production"
    fi
    rm -rf --one-file-system "$staging_directory"
  fi
}

cleanup_temporary_resources() {
  cleanup_candidate_container
  cleanup_staging
}

smoke_release() {
  local release="$1"
  local env_file="${release}/.env.production"
  local caddy_file="${release}/Caddyfile"

  if grep -qE '^rijvia\.be[[:space:]]*\{' "$caddy_file"; then
    "$SMOKE_SCRIPT" \
      --env-file "$env_file" \
      --frontend-url https://rijvia.be \
      --api-url https://api.rijvia.be
  else
    "$SMOKE_SCRIPT" \
      --env-file "$env_file" \
      --frontend-url https://readyroad.be \
      --api-url https://api.readyroad.be
  fi
}

finalize_result() {
  local status="$1"
  local health="$2"
  local rollback_status="$3"
  local manifest="${final_release}/release-manifest.env"
  local duration

  [[ -n "$final_release" && -f "$manifest" ]] || return 0
  duration=$(( $(date +%s) - started_epoch ))
  {
    printf 'DEPLOYMENT_STATUS=%s\n' "$status"
    printf 'HEALTH_RESULT=%s\n' "$health"
    printf 'ROLLBACK_RESULT=%s\n' "$rollback_status"
    printf 'DURATION_SECONDS=%s\n' "$duration"
    printf 'COMPLETED_AT=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  } >>"$manifest"
  chmod 0444 "$manifest"
}

handle_error() {
  local exit_code="$1"
  local rollback_result="NOT_REQUIRED"

  trap - ERR
  set +e
  cleanup_temporary_resources
  rr_log ERROR deployment_failed \
    "release=${RELEASE_ID:-unresolved} exit_code=${exit_code}"
  if (( activation_started == 1 )) && [[ -n "$rollback_target" ]]; then
    if "$ROLLBACK_SCRIPT" "$rollback_target" --lock-held; then
      rollback_result="PASSED"
      rr_log WARNING automatic_rollback "status=passed"
    else
      rollback_result="FAILED"
      rr_log CRITICAL automatic_rollback "status=failed"
    fi
  elif [[ ! -d "$final_release" ]]; then
    [[ -z "$backend_image" ]] ||
      docker image rm "$backend_image" >/dev/null 2>&1 || true
    [[ -z "$frontend_image" ]] ||
      docker image rm "$frontend_image" >/dev/null 2>&1 || true
  fi
  finalize_result FAILED FAILED "$rollback_result"
  if [[ "$rollback_result" == "FAILED" ]]; then
    exit 70
  fi
  exit "$exit_code"
}
trap 'handle_error $?' ERR
trap cleanup_temporary_resources EXIT

rollback_target="$(rr_release_path "$READYROAD_CURRENT_LINK")"
rr_validate_release_files "$rollback_target"
rr_log INFO deployment_started \
  "backend_ref=${BACKEND_REF} frontend_ref=${FRONTEND_REF} operator=${SUDO_USER:-root}"

smoke_release "$rollback_target"

if (( DRY_RUN == 1 )); then
  validate_remote_ref() {
    local repository="$1"
    local ref="$2"
    local checkout_directory status=0

    checkout_directory="$(mktemp -d /tmp/readyroad-ref-check.XXXXXX)"
    git init --quiet "$checkout_directory"
    git -C "$checkout_directory" remote add origin "$repository"
    GIT_TERMINAL_PROMPT=0 git -C "$checkout_directory" fetch \
      --quiet --depth=1 origin "$ref" || status=$?
    rm -rf --one-file-system "$checkout_directory"
    return "$status"
  }

  validate_remote_ref "$BACKEND_REPOSITORY" "$BACKEND_REF"
  validate_remote_ref "$FRONTEND_REPOSITORY" "$FRONTEND_REF"
  rr_prune_releases dry-run "$RELEASE_RETENTION" "$rollback_target" "$rollback_target"
  rr_log INFO deployment_dry_run "status=passed changes=0"
  exit 0
fi

if ! docker volume inspect readyroad-article-images >/dev/null 2>&1; then
  docker volume create readyroad-article-images >/dev/null
  rr_log INFO persistent_volume_created "volume=readyroad-article-images"
fi

staging_directory="$(mktemp -d "${READYROAD_RELEASES_DIR}/.staging.XXXXXX")"
chmod 0700 "$staging_directory"

checkout_ref() {
  local repository="$1"
  local ref="$2"
  local destination="$3"

  git init --quiet "$destination"
  git -C "$destination" remote add origin "$repository"
  git -C "$destination" fetch --quiet --depth=1 origin "$ref"
  git -C "$destination" checkout --quiet --detach FETCH_HEAD
  [[ -z "$(git -C "$destination" status --porcelain)" ]] ||
    rr_die "dirty_source_checkout"
}

checkout_ref "$BACKEND_REPOSITORY" "$BACKEND_REF" \
  "${staging_directory}/backend"
checkout_ref "$FRONTEND_REPOSITORY" "$FRONTEND_REF" \
  "${staging_directory}/frontend"
backend_commit="$(git -C "${staging_directory}/backend" rev-parse HEAD)"
frontend_commit="$(git -C "${staging_directory}/frontend" rev-parse HEAD)"

if [[ -z "$RELEASE_ID" ]]; then
  RELEASE_ID="$(date -u +%Y%m%d)-${backend_commit:0:7}-${frontend_commit:0:7}"
  rr_validate_release_id "$RELEASE_ID"
fi
final_release="${READYROAD_RELEASES_DIR}/${RELEASE_ID}"
[[ ! -e "$final_release" ]] || rr_die "release_already_exists"

cp "${TEMPLATE_DIR}/docker-compose.yml" \
  "${staging_directory}/docker-compose.yml"
cp "${TEMPLATE_DIR}/Caddyfile.production" \
  "${staging_directory}/Caddyfile"
cp "${TEMPLATE_DIR}/Caddyfile.production" \
  "${staging_directory}/Caddyfile.production"
cp "${rollback_target}/.env.production" \
  "${staging_directory}/.env.production"
chmod 0600 "${staging_directory}/.env.production"

backend_image="readyroad-backend:${RELEASE_ID}"
frontend_image="readyroad-frontend:${RELEASE_ID}"
rr_set_env_value "${staging_directory}/.env.production" \
  BACKEND_IMAGE_TAG "$RELEASE_ID"
rr_set_env_value "${staging_directory}/.env.production" \
  FRONTEND_IMAGE_TAG "$RELEASE_ID"

export BACKEND_IMAGE_TAG="$RELEASE_ID"
export FRONTEND_IMAGE_TAG="$RELEASE_ID"
docker compose \
  --project-directory "$staging_directory" \
  --env-file "${staging_directory}/.env.production" \
  --file "${staging_directory}/docker-compose.yml" \
  config --quiet

rr_log INFO image_build_started "component=backend"
docker compose \
  --project-directory "$staging_directory" \
  --env-file "${staging_directory}/.env.production" \
  --file "${staging_directory}/docker-compose.yml" \
  build backend
rr_log INFO image_build_started "component=frontend"
docker compose \
  --project-directory "$staging_directory" \
  --env-file "${staging_directory}/.env.production" \
  --file "${staging_directory}/docker-compose.yml" \
  build frontend

backend_image_id="$(docker image inspect --format '{{.Id}}' "$backend_image")"
frontend_image_id="$(docker image inspect --format '{{.Id}}' "$frontend_image")"
caddy_image="caddy:2.11.4-alpine"
caddy_image_id="$(docker image inspect --format '{{.Id}}' "$caddy_image")"
rr_verify_image "$backend_image" "$backend_image_id"
rr_verify_image "$frontend_image" "$frontend_image_id"
rr_verify_image "$caddy_image" "$caddy_image_id"

docker run --rm --entrypoint sh "$backend_image" \
  -ec 'test -s /app/app.jar && java -version >/dev/null'
candidate_frontend="readyroad-preflight-${frontend_commit:0:7}-$$"
docker run --detach --name "$candidate_frontend" \
  --network "$READYROAD_NETWORK" \
  --env BACKEND_URL=http://backend:8890/api \
  --env NEXT_PUBLIC_API_BASE_URL=http://backend:8890/api \
  --env NEXT_PUBLIC_APP_URL=https://rijvia.be \
  --env NODE_ENV=production \
  "$frontend_image" >/dev/null
rr_wait_exec_http "$candidate_frontend" \
  http://127.0.0.1:3000/api/health 120
cleanup_candidate_container

compose_sha="$(sha256sum "${staging_directory}/docker-compose.yml" | awk '{print $1}')"
caddy_sha="$(sha256sum "${staging_directory}/Caddyfile" | awk '{print $1}')"
created_at="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
operator="${SUDO_USER:-root}"
[[ "$operator" =~ ^[A-Za-z0-9._-]+$ ]] || operator=root
cat >"${staging_directory}/release-manifest.env" <<EOF
RELEASE_ID=${RELEASE_ID}
VERSION_IDENTIFIER=${RELEASE_ID}
BACKEND_GIT_COMMIT=${backend_commit}
FRONTEND_GIT_COMMIT=${frontend_commit}
BACKEND_SOURCE_REF=${BACKEND_REF}
FRONTEND_SOURCE_REF=${FRONTEND_REF}
BACKEND_IMAGE=${backend_image}
BACKEND_IMAGE_ID=${backend_image_id}
FRONTEND_IMAGE=${frontend_image}
FRONTEND_IMAGE_ID=${frontend_image_id}
CADDY_IMAGE=${caddy_image}
CADDY_IMAGE_ID=${caddy_image_id}
COMPOSE_SHA256=${compose_sha}
CADDY_SHA256=${caddy_sha}
DEPLOYMENT_STARTED_AT=${created_at}
ROLLBACK_TARGET=${rollback_target}
OPERATOR=${operator}
EOF
chmod 0600 "${staging_directory}/release-manifest.env"

mv "$staging_directory" "$final_release"
staging_directory=""
rr_log INFO release_prepared \
  "release=${RELEASE_ID} backend_commit=${backend_commit} frontend_commit=${frontend_commit}"

if [[ -x "$BACKUP_SCRIPT" ]]; then
  "$BACKUP_SCRIPT" --deployment
fi

activation_started=1
rr_compose "$final_release" up -d --no-build backend frontend
rr_wait_container_health readyroad-backend 420
rr_wait_container_health readyroad-frontend 180

if (( SIMULATE_HEALTH_FAILURE == 1 )); then
  rr_log WARNING simulated_health_failure "release=${RELEASE_ID}"
  false
fi

smoke_release "$rollback_target"
rr_atomic_current_link "$final_release"
rr_compose "$final_release" up -d --no-deps --no-build caddy
rr_wait_container_health readyroad-caddy 120
smoke_release "$final_release"

rr_prune_releases dry-run "$RELEASE_RETENTION" "$final_release" "$rollback_target"
rr_prune_releases execute "$RELEASE_RETENTION" "$final_release" "$rollback_target"
finalize_result SUCCESS PASSED NOT_REQUIRED
duration=$(( $(date +%s) - started_epoch ))
rr_log INFO deployment_completed \
  "release=${RELEASE_ID} duration_seconds=${duration} health=passed"
activation_started=0

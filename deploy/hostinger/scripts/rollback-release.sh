#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
if [[ -r /opt/readyroad/bin/readyroad-deploy-lib ]]; then
  # shellcheck source=/dev/null
  source /opt/readyroad/bin/readyroad-deploy-lib
  readonly SMOKE_SCRIPT="/opt/readyroad/bin/readyroad-smoke"
else
  # shellcheck source=deploy-lib.sh
  source "${SCRIPT_DIR}/deploy-lib.sh"
  readonly SMOKE_SCRIPT="${SCRIPT_DIR}/production-smoke.sh"
fi

TARGET_RELEASE="${1:-}"
MODE="${2:-}"
LOCK_HELD=0
started_epoch="$(date +%s)"

usage() {
  printf 'Usage: %s /opt/readyroad/releases/<release> [--dry-run|--lock-held]\n' \
    "$0" >&2
  exit 2
}

[[ "$EUID" -eq 0 ]] || {
  printf 'readyroad-rollback must run as root.\n' >&2
  exit 1
}
[[ -n "$TARGET_RELEASE" ]] || usage
case "$MODE" in
  "") ;;
  --dry-run) ;;
  --lock-held) LOCK_HELD=1 ;;
  *) usage ;;
esac

install -d -o root -g adm -m 0750 "$READYROAD_LOG_DIR"
READYROAD_DEPLOY_LOG="${READYROAD_LOG_DIR}/rollback-$(date -u +%Y%m%dT%H%M%SZ).log"
export READYROAD_DEPLOY_LOG
touch "$READYROAD_DEPLOY_LOG"
chown root:adm "$READYROAD_DEPLOY_LOG"
chmod 0640 "$READYROAD_DEPLOY_LOG"

if (( LOCK_HELD == 0 )); then
  exec 9>/run/lock/readyroad-deploy.lock
  if ! flock -n 9; then
    rr_die "deployment_or_rollback_already_running"
  fi
fi

rr_require_commands docker curl jq flock grep readlink
TARGET_RELEASE="$(rr_release_path "$TARGET_RELEASE")"
rr_validate_release_files "$TARGET_RELEASE"
MANIFEST_FILE="${TARGET_RELEASE}/release-manifest.env"

BACKEND_IMAGE="$(rr_manifest_value BACKEND_IMAGE "$MANIFEST_FILE")"
BACKEND_IMAGE_ID="$(rr_manifest_value BACKEND_IMAGE_ID "$MANIFEST_FILE")"
FRONTEND_IMAGE="$(rr_manifest_value FRONTEND_IMAGE "$MANIFEST_FILE")"
FRONTEND_IMAGE_ID="$(rr_manifest_value FRONTEND_IMAGE_ID "$MANIFEST_FILE")"
CADDY_IMAGE="$(rr_manifest_value CADDY_IMAGE "$MANIFEST_FILE")"
CADDY_IMAGE_ID="$(rr_manifest_value CADDY_IMAGE_ID "$MANIFEST_FILE")"

[[ "$BACKEND_IMAGE" == readyroad-backend:* ]] ||
  rr_die "invalid_backend_image"
[[ "$FRONTEND_IMAGE" == readyroad-frontend:* ]] ||
  rr_die "invalid_frontend_image"
[[ "$CADDY_IMAGE" == caddy:* ]] ||
  rr_die "invalid_caddy_image"
rr_verify_image "$BACKEND_IMAGE" "$BACKEND_IMAGE_ID"
rr_verify_image "$FRONTEND_IMAGE" "$FRONTEND_IMAGE_ID"
rr_verify_image "$CADDY_IMAGE" "$CADDY_IMAGE_ID"
rr_compose "$TARGET_RELEASE" config --quiet

CURRENT_RELEASE="$(rr_release_path "$READYROAD_CURRENT_LINK")"
rr_log INFO rollback_validated \
  "current=$(basename "$CURRENT_RELEASE") target=$(basename "$TARGET_RELEASE")"
if [[ "$MODE" == "--dry-run" ]]; then
  rr_log INFO rollback_dry_run "status=passed changes=0"
  exit 0
fi

install -d -o root -g root -m 0700 "$READYROAD_STATE_DIR"
umask 077
STATE_FILE="${READYROAD_STATE_DIR}/rollback-$(date -u +%Y%m%dT%H%M%SZ).env"
{
  printf 'PRE_ROLLBACK_RELEASE=%s\n' "$CURRENT_RELEASE"
  printf 'ROLLBACK_TARGET=%s\n' "$TARGET_RELEASE"
  printf 'BACKEND_CONTAINER_IMAGE=%s\n' \
    "$(docker inspect --format '{{.Config.Image}}' readyroad-backend)"
  printf 'BACKEND_CONTAINER_IMAGE_ID=%s\n' \
    "$(docker inspect --format '{{.Image}}' readyroad-backend)"
  printf 'FRONTEND_CONTAINER_IMAGE=%s\n' \
    "$(docker inspect --format '{{.Config.Image}}' readyroad-frontend)"
  printf 'FRONTEND_CONTAINER_IMAGE_ID=%s\n' \
    "$(docker inspect --format '{{.Image}}' readyroad-frontend)"
  printf 'CADDY_CONTAINER_IMAGE=%s\n' \
    "$(docker inspect --format '{{.Config.Image}}' readyroad-caddy)"
  printf 'CADDY_CONTAINER_IMAGE_ID=%s\n' \
    "$(docker inspect --format '{{.Image}}' readyroad-caddy)"
} >"$STATE_FILE"

rr_log WARNING rollback_started "target=$(basename "$TARGET_RELEASE")"
rr_compose "$TARGET_RELEASE" up -d --no-build backend frontend
rr_wait_container_health readyroad-backend 420
rr_wait_container_health readyroad-frontend 180
rr_atomic_current_link "$TARGET_RELEASE"
rr_compose "$TARGET_RELEASE" up -d --no-deps --no-build caddy
rr_wait_container_health readyroad-caddy 120
"$SMOKE_SCRIPT" \
  --env-file "${TARGET_RELEASE}/.env.production" \
  --frontend-url https://rijvia.be \
  --api-url https://api.rijvia.be

duration=$(( $(date +%s) - started_epoch ))
{
  printf 'ROLLBACK_STATUS=SUCCESS\n'
  printf 'ROLLBACK_DURATION_SECONDS=%s\n' "$duration"
  printf 'COMPLETED_AT=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
} >>"$STATE_FILE"
rr_log INFO rollback_completed \
  "target=$(basename "$TARGET_RELEASE") duration_seconds=${duration}"

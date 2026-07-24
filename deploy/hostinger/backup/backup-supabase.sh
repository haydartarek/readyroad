#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
# shellcheck source=backup-lib.sh
if [[ -r /usr/local/lib/readyroad-backup/backup-lib.sh ]]; then
  # shellcheck source=/dev/null
  source /usr/local/lib/readyroad-backup/backup-lib.sh
else
  # shellcheck disable=SC1091
  source "${SCRIPT_DIR}/backup-lib.sh"
fi

readonly DATABASE_ENV_FILE="${DATABASE_ENV_FILE:-/etc/readyroad-backup/database.env}"
readonly POSTGRES_CLIENT_IMAGE="${POSTGRES_CLIENT_IMAGE:-postgres@sha256:742f40ea20b9ff2ff31db5458d127452988a2164df9e17441e191f3b72252193}"
readonly DATABASE_SCHEMA="${DATABASE_SCHEMA:-readyroad}"
readonly DAILY_RETENTION="${DAILY_RETENTION:-7}"
readonly WEEKLY_RETENTION="${WEEKLY_RETENTION:-4}"
readonly MONTHLY_RETENTION="${MONTHLY_RETENTION:-3}"

mode="${1:-backup}"
lock_file="/run/lock/readyroad-supabase-backup.lock"
stage_dir=""

cleanup() {
  if [[ -n "$stage_dir" && -d "$stage_dir" ]]; then
    find "$stage_dir" -type f -exec shred -u -- {} + 2>/dev/null || true
    rm -rf -- "$stage_dir"
  fi
}
trap cleanup EXIT

exec 9>"$lock_file"
if ! flock -n 9; then
  log_event WARNING backup_lock "component=database status=busy"
  exit 75
fi

ensure_backup_layout

if [[ "$mode" == "--retention-dry-run" ]]; then
  prune_artifacts "${BACKUP_ROOT}/db/daily" "readyroad-db-" \
    "$DAILY_RETENTION" dry-run
  prune_artifacts "${BACKUP_ROOT}/db/weekly" "readyroad-db-" \
    "$WEEKLY_RETENTION" dry-run
  prune_artifacts "${BACKUP_ROOT}/db/monthly" "readyroad-db-" \
    "$MONTHLY_RETENTION" dry-run
  exit 0
fi

if [[ "$mode" == "--simulate-failure" ]]; then
  log_event CRITICAL backup_failure \
    "component=database simulation=true"
  exit 97
fi

[[ "$mode" == "backup" ]] || {
  printf 'Usage: %s [backup|--retention-dry-run|--simulate-failure]\n' \
    "$0" >&2
  exit 2
}

for command in docker age sha256sum pg_restore flock; do
  if [[ "$command" == "pg_restore" ]]; then
    continue
  fi
  require_command "$command"
done
require_file "$DATABASE_ENV_FILE"
check_free_space 2097152

timestamp="$(date -u +%Y%m%d-%H%M%S)"
created_at="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
stage_dir="${BACKUP_ROOT}/.staging/db-${timestamp}-$$"
mkdir -m 0700 "$stage_dir"

plaintext="${stage_dir}/readyroad-db-${timestamp}.dump"
daily_dir="${BACKUP_ROOT}/db/daily"
encrypted="${daily_dir}/readyroad-db-${timestamp}.dump.age"

log_event INFO backup_started \
  "component=database timestamp=${created_at} schema=${DATABASE_SCHEMA}"

docker run --rm --pull=never \
  --env-file "$DATABASE_ENV_FILE" \
  "$POSTGRES_CLIENT_IMAGE" sh -ec '
    export PGPASSWORD="$POSTGRES_PASSWORD"
    export PGSSLMODE="${POSTGRES_SSL_MODE:-require}"
    exec pg_dump \
      --host "$POSTGRES_HOST" \
      --port "$POSTGRES_PORT" \
      --username "$POSTGRES_USERNAME" \
      --dbname "$POSTGRES_DATABASE" \
      --format custom \
      --compress 6 \
      --schema "'"$DATABASE_SCHEMA"'" \
      --no-owner \
      --no-privileges \
      --lock-wait-timeout 10s
  ' >"$plaintext"
chmod 0600 "$plaintext"
require_file "$plaintext"

docker run --rm --pull=never \
  --volume "${stage_dir}:/backup:ro" \
  "$POSTGRES_CLIENT_IMAGE" \
  pg_restore --list "/backup/$(basename "$plaintext")" >/dev/null

encrypt_artifact "$plaintext" "$encrypted" database "$created_at"
verify_encrypted_checksum "$encrypted"

day_of_week="$(date -u +%u)"
day_of_month="$(date -u +%d)"
if [[ "$day_of_week" == "7" ]]; then
  copy_artifact_set "$encrypted" "${BACKUP_ROOT}/db/weekly"
fi
if [[ "$day_of_month" == "01" ]]; then
  copy_artifact_set "$encrypted" "${BACKUP_ROOT}/db/monthly"
fi

prune_artifacts "$daily_dir" "readyroad-db-" "$DAILY_RETENTION"
prune_artifacts "${BACKUP_ROOT}/db/weekly" "readyroad-db-" \
  "$WEEKLY_RETENTION"
prune_artifacts "${BACKUP_ROOT}/db/monthly" "readyroad-db-" \
  "$MONTHLY_RETENTION"

atomic_write "${BACKUP_STATE_DIR}/latest-backup" "$encrypted"
atomic_write "${BACKUP_STATE_DIR}/last-db-success" "$(date +%s)"

log_event INFO backup_completed \
  "component=database file=$(basename "$encrypted") bytes=$(stat -c %s "$encrypted")"

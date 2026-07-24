#!/usr/bin/env bash

set -Eeuo pipefail

readonly BACKUP_STATE_DIR="${BACKUP_STATE_DIR:-/var/lib/readyroad-backup/state}"
readonly DATABASE_BACKUP="${DATABASE_BACKUP:-/usr/local/sbin/readyroad-backup-supabase}"
readonly CONFIG_BACKUP="${CONFIG_BACKUP:-/usr/local/sbin/readyroad-backup-config}"
readonly MODE="${1:-scheduled}"

mkdir -p "$BACKUP_STATE_DIR"
chmod 0700 "$BACKUP_STATE_DIR"
umask 077

exec 9>/run/lock/readyroad-backup-run.lock
if ! flock -n 9; then
  logger --tag readyroad-backup -- \
    "level=WARNING event=backup_lock component=orchestrator status=busy"
  exit 75
fi

success=false
finish() {
  local exit_code=$?

  if [[ "$success" == "true" ]]; then
    printf '%s\n' "$(date +%s)" >"${BACKUP_STATE_DIR}/last-success"
    rm -f -- "${BACKUP_STATE_DIR}/last-failure"
    logger --tag readyroad-backup -- \
      "level=INFO event=backup_cycle_completed status=success"
  else
    printf '%s\n' "$(date +%s)" >"${BACKUP_STATE_DIR}/last-failure"
    logger --tag readyroad-backup -- \
      "level=CRITICAL event=backup_cycle_failed exit_code=${exit_code}"
  fi
}
trap finish EXIT

if [[ "$MODE" == "--simulate-failure" ]]; then
  "$DATABASE_BACKUP" --simulate-failure
fi

[[ "$MODE" == "scheduled" || "$MODE" == "--force" ]] || {
  printf 'Usage: %s [scheduled|--force|--simulate-failure]\n' "$0" >&2
  exit 2
}

"$DATABASE_BACKUP"
if [[ "$MODE" == "--force" ]]; then
  "$CONFIG_BACKUP" --force
else
  "$CONFIG_BACKUP" scheduled
fi

success=true

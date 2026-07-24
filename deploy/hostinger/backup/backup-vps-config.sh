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

readonly WEEKLY_RETENTION="${WEEKLY_CONFIG_RETENTION:-4}"
readonly DEPLOYMENT_RETENTION="${DEPLOYMENT_CONFIG_RETENTION:-5}"

mode="${1:-scheduled}"
series="weekly"
lock_file="/run/lock/readyroad-config-backup.lock"
stage_dir=""

cleanup() {
  if [[ -n "$stage_dir" && -d "$stage_dir" ]]; then
    find "$stage_dir" -type f -exec shred -u -- {} + 2>/dev/null || true
    rm -rf -- "$stage_dir"
  fi
}
trap cleanup EXIT

copy_path() {
  local source="$1"
  local destination="$2"

  if [[ -e "$source" || -L "$source" ]]; then
    cp -a --parents "$source" "$destination"
  fi
}

archive_and_encrypt() {
  local tree="$1"
  local output_directory="$2"
  local prefix="$3"
  local timestamp="$4"
  local created_at="$5"
  local plaintext="${stage_dir}/${prefix}-${timestamp}.tar.zst"
  local encrypted="${output_directory}/${prefix}-${timestamp}.tar.zst.age"

  tar --zstd --numeric-owner -C "$tree" -cf "$plaintext" .
  chmod 0600 "$plaintext"
  encrypt_artifact "$plaintext" "$encrypted" "$prefix" "$created_at"
  verify_encrypted_checksum "$encrypted"
}

exec 9>"$lock_file"
if ! flock -n 9; then
  log_event WARNING backup_lock "component=config status=busy"
  exit 75
fi

ensure_backup_layout

case "$mode" in
  --retention-dry-run)
    for prefix in readyroad-config readyroad-environment readyroad-caddy \
      readyroad-uploads; do
      prune_artifacts "${BACKUP_ROOT}/config/weekly" "${prefix}-" \
        "$WEEKLY_RETENTION" dry-run
      prune_artifacts "${BACKUP_ROOT}/config/deployment" "${prefix}-" \
        "$DEPLOYMENT_RETENTION" dry-run
    done
    exit 0
    ;;
  --simulate-failure)
    log_event CRITICAL backup_failure \
      "component=config simulation=true"
    exit 97
    ;;
  --deployment)
    series="deployment"
    ;;
  --force)
    series="weekly"
    ;;
  scheduled)
    if [[ "$(date -u +%u)" != "7" ]]; then
      log_event INFO backup_skipped \
        "component=config reason=not_weekly_schedule"
      exit 0
    fi
    ;;
  *)
    printf 'Usage: %s [scheduled|--force|--deployment|--retention-dry-run|--simulate-failure]\n' \
      "$0" >&2
    exit 2
    ;;
esac

for command in age docker sha256sum tar zstd flock; do
  require_command "$command"
done
check_free_space 2097152

timestamp="$(date -u +%Y%m%d-%H%M%S)"
created_at="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
stage_dir="${BACKUP_ROOT}/.staging/config-${timestamp}-$$"
config_tree="${stage_dir}/config"
env_tree="${stage_dir}/environment"
caddy_tree="${stage_dir}/caddy"
uploads_tree="${stage_dir}/uploads"
output_directory="${BACKUP_ROOT}/config/${series}"

mkdir -p \
  "$config_tree/inventory" "$env_tree" "$caddy_tree" "$uploads_tree" \
  "$output_directory"
chmod 0700 \
  "$config_tree" "$config_tree/inventory" "$env_tree" "$caddy_tree" \
  "$uploads_tree"

log_event INFO backup_started \
  "component=config timestamp=${created_at} series=${series}"

for release in /opt/readyroad/releases/*; do
  [[ -d "$release" ]] || continue
  for name in docker-compose.yml Caddyfile Caddyfile.production \
    Caddyfile.pre-gate5b release-manifest.env; do
    copy_path "${release}/${name}" "$config_tree"
  done
done

for path in \
  /opt/readyroad/bin \
  /opt/readyroad/rollback-state \
  /usr/local/sbin/readyroad-monitor \
  /usr/local/sbin/readyroad-backup-run \
  /usr/local/sbin/readyroad-backup-supabase \
  /usr/local/sbin/readyroad-backup-config \
  /usr/local/lib/readyroad-backup \
  /etc/readyroad-backup/backup.conf \
  /etc/readyroad-backup/age-recipient.txt \
  /etc/systemd/system/readyroad-monitor.service \
  /etc/systemd/system/readyroad-monitor.timer \
  /etc/systemd/system/readyroad-backup.service \
  /etc/systemd/system/readyroad-backup.timer \
  /etc/systemd/journald.conf.d \
  /etc/docker/daemon.json \
  /etc/ufw/user.rules \
  /etc/ufw/user6.rules \
  /etc/fail2ban/jail.local \
  /etc/fail2ban/jail.d \
  /etc/ssh/sshd_config \
  /etc/ssh/sshd_config.d; do
  copy_path "$path" "$config_tree"
done

readlink -f /opt/readyroad/current \
  >"${config_tree}/inventory/current-release.txt"
docker volume inspect \
  readyroad-backend-logs readyroad-backups readyroad-caddy-data \
  readyroad-caddy-config readyroad-uploads \
  >"${config_tree}/inventory/docker-volumes.json"
docker image inspect \
  "$(docker inspect -f '{{.Image}}' readyroad-backend)" \
  "$(docker inspect -f '{{.Image}}' readyroad-frontend)" \
  "$(docker inspect -f '{{.Image}}' readyroad-caddy)" \
  >"${config_tree}/inventory/docker-images.json"
docker ps --format \
  '{{.Names}}|{{.Image}}|{{.Status}}|{{.Ports}}' \
  >"${config_tree}/inventory/docker-containers.txt"

current_release="$(readlink -f /opt/readyroad/current)"
copy_path "${current_release}/.env.production" "$env_tree"
copy_path /etc/readyroad-backup/database.env "$env_tree"

copy_path \
  /var/lib/docker/volumes/readyroad-caddy-data/_data "$caddy_tree"
copy_path \
  /var/lib/docker/volumes/readyroad-caddy-config/_data "$caddy_tree"

archive_and_encrypt \
  "$config_tree" "$output_directory" "readyroad-config" \
  "$timestamp" "$created_at"
archive_and_encrypt \
  "$env_tree" "$output_directory" "readyroad-environment" \
  "$timestamp" "$created_at"
archive_and_encrypt \
  "$caddy_tree" "$output_directory" "readyroad-caddy" \
  "$timestamp" "$created_at"

uploads_path="/var/lib/docker/volumes/readyroad-uploads/_data"
upload_files="$(find "$uploads_path" -type f 2>/dev/null | wc -l | tr -d ' ')"
if (( upload_files > 0 )); then
  copy_path "$uploads_path" "$uploads_tree"
  archive_and_encrypt \
    "$uploads_tree" "$output_directory" "readyroad-uploads" \
    "$timestamp" "$created_at"
else
  log_event INFO backup_skipped \
    "component=uploads reason=empty_volume"
fi

if [[ "$series" == "weekly" ]]; then
  retention="$WEEKLY_RETENTION"
else
  retention="$DEPLOYMENT_RETENTION"
fi
for prefix in readyroad-config readyroad-environment readyroad-caddy \
  readyroad-uploads; do
  prune_artifacts "$output_directory" "${prefix}-" "$retention"
done

atomic_write "${BACKUP_STATE_DIR}/last-config-success" "$(date +%s)"
log_event INFO backup_completed \
  "component=config series=${series} uploads=${upload_files}"

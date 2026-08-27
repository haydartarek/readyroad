#!/usr/bin/env bash

set -uo pipefail

readonly STATE_DIR="${STATE_DIRECTORY:-/var/lib/readyroad-monitor}"
readonly CPU_WARNING="${CPU_WARNING:-80}"
readonly RAM_WARNING="${RAM_WARNING:-80}"
readonly RAM_CRITICAL="${RAM_CRITICAL:-90}"
readonly DISK_WARNING="${DISK_WARNING:-75}"
readonly DISK_CRITICAL="${DISK_CRITICAL:-85}"
readonly INODE_WARNING="${INODE_WARNING:-75}"
readonly SSL_WARNING_DAYS="${SSL_WARNING_DAYS:-30}"
readonly SSL_CRITICAL_DAYS="${SSL_CRITICAL_DAYS:-14}"
readonly FIVE_XX_WARNING="${FIVE_XX_WARNING:-3}"
readonly LOG_BYTES_WARNING="${LOG_BYTES_WARNING:-524288000}"
readonly SUSTAINED_CHECKS="${SUSTAINED_CHECKS:-3}"
readonly BACKUP_STATE_DIR="${BACKUP_STATE_DIR:-/var/lib/readyroad-backup/state}"
readonly BACKUP_MAX_AGE_SECONDS="${BACKUP_MAX_AGE_SECONDS:-93600}"
readonly RESTORE_MAX_AGE_SECONDS="${RESTORE_MAX_AGE_SECONDS:-3024000}"
readonly SIMULATION="${1:-none}"
readonly EXPECTED_CONTAINERS=(
  readyroad-caddy
  readyroad-frontend
  readyroad-backend
)
readonly TLS_HOSTS=(
  rijvia.be
  www.rijvia.be
  api.rijvia.be
)

alert_count=0

mkdir -p "$STATE_DIR"
chmod 0700 "$STATE_DIR"
umask 077

emit() {
  local level="$1"
  local event="$2"
  shift 2
  local message="$*"

  if [[ -z "${JOURNAL_STREAM:-}" ]]; then
    logger --tag readyroad-monitor -- \
      "level=${level} event=${event} ${message}"
  fi
  printf 'level=%s event=%s %s\n' "$level" "$event" "$message"
}

alert() {
  alert_count=$((alert_count + 1))
  emit "$@"
}

read_counter() {
  local path="$1"
  local value=0

  if [[ -r "$path" ]]; then
    read -r value <"$path" || value=0
  fi
  [[ "$value" =~ ^[0-9]+$ ]] || value=0
  printf '%s' "$value"
}

track_sustained_threshold() {
  local key="$1"
  local value="$2"
  local threshold="$3"
  local path="${STATE_DIR}/${key}.streak"
  local streak

  streak="$(read_counter "$path")"
  if (( value >= threshold )); then
    streak=$((streak + 1))
  else
    streak=0
  fi
  printf '%s\n' "$streak" >"$path"

  if (( streak >= SUSTAINED_CHECKS )); then
    alert WARNING resource_threshold \
      "metric=${key} value=${value} threshold=${threshold} consecutive=${streak}"
  fi
}

integer_percent() {
  awk -v used="$1" -v total="$2" \
    'BEGIN { if (total <= 0) print 0; else printf "%.0f", (used * 100) / total }'
}

cpu_percent=0
if command -v sar >/dev/null 2>&1; then
  cpu_percent="$(
    LC_ALL=C sar -u 1 1 2>/dev/null |
      awk '$1 == "Average:" && $2 == "all" { printf "%.0f", 100 - $NF }'
  )"
fi
[[ "$cpu_percent" =~ ^[0-9]+$ ]] || cpu_percent=0
track_sustained_threshold cpu_percent "$cpu_percent" "$CPU_WARNING"

read -r memory_total memory_available < <(
  awk '
    /^MemTotal:/ { total=$2 }
    /^MemAvailable:/ { available=$2 }
    END { print total+0, available+0 }
  ' /proc/meminfo
)
memory_used=$((memory_total - memory_available))
ram_percent="$(integer_percent "$memory_used" "$memory_total")"
if (( ram_percent >= RAM_CRITICAL )); then
  alert CRITICAL resource_threshold \
    "metric=ram_percent value=${ram_percent} threshold=${RAM_CRITICAL}"
elif (( ram_percent >= RAM_WARNING )); then
  alert WARNING resource_threshold \
    "metric=ram_percent value=${ram_percent} threshold=${RAM_WARNING}"
fi

load_one="$(awk '{ print $1 }' /proc/loadavg)"
cpu_count="$(nproc)"
load_percent="$(
  awk -v load_value="$load_one" -v cpus="$cpu_count" \
    'BEGIN { if (cpus <= 0) print 0; else printf "%.0f", (load_value * 100) / cpus }'
)"
track_sustained_threshold load_percent "$load_percent" 100

check_filesystem() {
  local path="$1"
  local label="$2"
  local disk_percent inode_percent

  disk_percent="$(
    df -P "$path" |
      awk 'NR == 2 { gsub(/%/, "", $5); print $5 }'
  )"
  inode_percent="$(
    df -Pi "$path" |
      awk 'NR == 2 { gsub(/%/, "", $5); print $5 }'
  )"

  [[ "$disk_percent" =~ ^[0-9]+$ ]] || disk_percent=0
  [[ "$inode_percent" =~ ^[0-9]+$ ]] || inode_percent=0

  if (( disk_percent >= DISK_CRITICAL )); then
    alert CRITICAL resource_threshold \
      "metric=disk_percent filesystem=${label} value=${disk_percent} threshold=${DISK_CRITICAL}"
  elif (( disk_percent >= DISK_WARNING )); then
    alert WARNING resource_threshold \
      "metric=disk_percent filesystem=${label} value=${disk_percent} threshold=${DISK_WARNING}"
  fi

  if (( inode_percent >= INODE_WARNING )); then
    alert WARNING resource_threshold \
      "metric=inode_percent filesystem=${label} value=${inode_percent} threshold=${INODE_WARNING}"
  fi

  printf '%s %s' "$disk_percent" "$inode_percent"
}

read -r root_disk_percent root_inode_percent < <(
  check_filesystem / root
)
read -r docker_disk_percent docker_inode_percent < <(
  check_filesystem /var/lib/docker docker
)

boot_id="$(cat /proc/sys/kernel/random/boot_id)"
boot_id_file="${STATE_DIR}/boot_id"
if [[ -r "$boot_id_file" ]]; then
  read -r previous_boot_id <"$boot_id_file" || previous_boot_id=""
  if [[ -n "$previous_boot_id" && "$previous_boot_id" != "$boot_id" ]]; then
    alert INFO reboot_detected "boot_id_changed=true"
  fi
fi
printf '%s\n' "$boot_id" >"$boot_id_file"

if ! systemctl is-active --quiet docker; then
  alert CRITICAL docker_daemon "status=inactive"
else
  for container in "${EXPECTED_CONTAINERS[@]}"; do
    inspection="$(
      docker inspect --format \
        '{{.State.Status}} {{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}} {{.RestartCount}}' \
        "$container" 2>/dev/null || true
    )"

    if [[ -z "$inspection" ]]; then
      alert CRITICAL container_missing "container=${container}"
      continue
    fi

    read -r container_status health_status restart_count <<<"$inspection"
    if [[ "$container_status" != "running" ]]; then
      alert CRITICAL container_stopped \
        "container=${container} status=${container_status}"
    elif [[ "$health_status" != "healthy" ]]; then
      alert CRITICAL container_unhealthy \
        "container=${container} health=${health_status}"
    fi

    restart_file="${STATE_DIR}/${container}.restarts"
    previous_restarts="$(read_counter "$restart_file")"
    if (( restart_count > previous_restarts )); then
      restart_delta=$((restart_count - previous_restarts))
      if (( restart_delta >= 3 )); then
        alert CRITICAL container_restart_loop \
          "container=${container} delta=${restart_delta} total=${restart_count}"
      else
        alert WARNING container_restarted \
          "container=${container} delta=${restart_delta} total=${restart_count}"
      fi
    fi
    printf '%s\n' "$restart_count" >"$restart_file"
  done

  while IFS= read -r running_container; do
    [[ -n "$running_container" ]] || continue
    is_expected=false
    for expected in "${EXPECTED_CONTAINERS[@]}"; do
      if [[ "$running_container" == "$expected" ]]; then
        is_expected=true
        break
      fi
    done
    if [[ "$is_expected" == "false" ]]; then
      alert WARNING unexpected_container \
        "container=${running_container}"
    fi
  done < <(docker ps --format '{{.Names}}')
fi

if ! curl --fail --silent --show-error --max-time 10 \
  http://127.0.0.1:8890/actuator/health |
  jq -e '.status == "UP"' >/dev/null 2>&1; then
  alert CRITICAL backend_health "status=failed"
fi

if ! curl --fail --silent --show-error --max-time 10 \
  http://127.0.0.1:3000/api/health >/dev/null 2>&1; then
  alert CRITICAL frontend_health "status=failed"
fi

five_xx_count="$(
  docker logs --since 5m readyroad-caddy 2>&1 |
    jq -Rrc 'fromjson? | select(.status >= 500) | 1' |
    wc -l |
    tr -d ' '
)"
[[ "$five_xx_count" =~ ^[0-9]+$ ]] || five_xx_count=0
if (( five_xx_count >= FIVE_XX_WARNING )); then
  alert WARNING http_5xx \
    "window=5m count=${five_xx_count} threshold=${FIVE_XX_WARNING}"
fi

sensitive_log_count="$(
  {
    docker logs --since 5m readyroad-caddy 2>&1
    docker logs --since 5m readyroad-frontend 2>&1
    docker logs --since 5m readyroad-backend 2>&1
  } |
    grep -Ei \
      'authorization.*(bearer|basic)|password=|jwt_secret|smtp_password|client_secret|postgres(ql)?://[^ ]+:[^ ]+@|[?&](token|code|state|access_token|refresh_token|id_token|api_key|apikey|secret|password)=' |
    grep -Eivc 'REDACTED' |
    tr -d ' '
)"
[[ "$sensitive_log_count" =~ ^[0-9]+$ ]] || sensitive_log_count=0
if (( sensitive_log_count > 0 )); then
  alert CRITICAL sensitive_log_pattern \
    "window=5m count=${sensitive_log_count}"
fi

log_bytes="$(du -sb /var/lib/docker/containers 2>/dev/null | awk '{ print $1 }')"
[[ "$log_bytes" =~ ^[0-9]+$ ]] || log_bytes=0
if (( log_bytes >= LOG_BYTES_WARNING )); then
  alert WARNING excessive_log_growth \
    "bytes=${log_bytes} threshold=${LOG_BYTES_WARNING}"
fi

now_epoch="$(date +%s)"
minimum_ssl_days=9999
for host in "${TLS_HOSTS[@]}"; do
  certificate_end="$(
    timeout 15 openssl s_client \
      -servername "$host" \
      -connect "${host}:443" </dev/null 2>/dev/null |
      openssl x509 -noout -enddate 2>/dev/null |
      cut -d= -f2- || true
  )"

  if [[ -z "$certificate_end" ]]; then
    alert CRITICAL ssl_check "host=${host} status=unavailable"
    continue
  fi

  certificate_epoch="$(date -d "$certificate_end" +%s 2>/dev/null || printf '0')"
  if (( certificate_epoch <= 0 )); then
    alert CRITICAL ssl_check "host=${host} status=invalid_expiry"
    continue
  fi

  days_remaining=$(((certificate_epoch - now_epoch) / 86400))
  if (( days_remaining < minimum_ssl_days )); then
    minimum_ssl_days="$days_remaining"
  fi

  if (( days_remaining <= SSL_CRITICAL_DAYS )); then
    alert CRITICAL ssl_expiry \
      "host=${host} days_remaining=${days_remaining} threshold=${SSL_CRITICAL_DAYS}"
  elif (( days_remaining <= SSL_WARNING_DAYS )); then
    alert WARNING ssl_expiry \
      "host=${host} days_remaining=${days_remaining} threshold=${SSL_WARNING_DAYS}"
  fi
done

if ! ufw status 2>/dev/null | grep -q '^Status: active'; then
  alert CRITICAL firewall "ufw=inactive"
fi

if ! fail2ban-client status sshd >/dev/null 2>&1; then
  alert WARNING security_service "fail2ban_sshd=unavailable"
fi

backup_last_success="$(read_counter "${BACKUP_STATE_DIR}/last-success")"
backup_last_failure="$(read_counter "${BACKUP_STATE_DIR}/last-failure")"
restore_last_success="$(read_counter "${BACKUP_STATE_DIR}/last-restore-success")"
current_epoch="$(date +%s)"

if (( backup_last_failure > backup_last_success )); then
  alert CRITICAL backup_failure \
    "last_failure=${backup_last_failure} last_success=${backup_last_success}"
fi

if (( backup_last_success == 0 )); then
  alert WARNING backup_stale "status=never_completed"
elif (( current_epoch - backup_last_success > BACKUP_MAX_AGE_SECONDS )); then
  alert WARNING backup_stale \
    "age_seconds=$((current_epoch - backup_last_success)) threshold=${BACKUP_MAX_AGE_SECONDS}"
fi

latest_backup_file="${BACKUP_STATE_DIR}/latest-backup"
if [[ -r "$latest_backup_file" ]]; then
  read -r latest_backup <"$latest_backup_file" || latest_backup=""
  if [[ -z "$latest_backup" || ! -s "$latest_backup" ]]; then
    alert CRITICAL backup_missing "latest_path_valid=false"
  elif [[ ! -s "${latest_backup}.sha256" ]] ||
    ! (
      cd "$(dirname "$latest_backup")" &&
        sha256sum --check --status "$(basename "${latest_backup}.sha256")"
    ); then
    alert CRITICAL backup_checksum "status=failed"
  fi
else
  alert WARNING backup_missing "state_file=missing"
fi

if systemctl is-failed --quiet readyroad-backup.service; then
  alert CRITICAL backup_timer "service=failed"
fi

if (( restore_last_success == 0 )); then
  alert WARNING restore_overdue "status=never_verified"
elif (( current_epoch - restore_last_success > RESTORE_MAX_AGE_SECONDS )); then
  alert WARNING restore_overdue \
    "age_seconds=$((current_epoch - restore_last_success)) threshold=${RESTORE_MAX_AGE_SECONDS}"
fi

unexpected_public_ports="$(
  ss -H -lntu |
    awk '$5 ~ /^0\.0\.0\.0:/ || $5 ~ /^\[::\]:/ {
      value=$5
      sub(/^.*:/, "", value)
      if (value != "22" && value != "80" && value != "443") print value
    }' |
    sort -u |
    paste -sd, -
)"
if [[ -n "$unexpected_public_ports" ]]; then
  alert CRITICAL unexpected_public_port \
    "ports=${unexpected_public_ports}"
fi

if [[ "$SIMULATION" != "none" ]]; then
  case "$SIMULATION" in
    container|disk|ssl|5xx|security|backup)
      alert WARNING simulated_alert "type=${SIMULATION}"
      ;;
    *)
      alert WARNING simulated_alert "type=unknown"
      ;;
  esac
fi

network_bytes="$(
  awk -F'[: ]+' '
    $1 != "lo" && NF >= 11 { rx += $3; tx += $11 }
    END { print rx+0, tx+0 }
  ' /proc/net/dev
)"
uptime_seconds="$(awk '{ printf "%.0f", $1 }' /proc/uptime)"

emit INFO summary \
  "alerts=${alert_count} cpu_percent=${cpu_percent} ram_percent=${ram_percent} load_percent=${load_percent} root_disk_percent=${root_disk_percent} docker_disk_percent=${docker_disk_percent} root_inode_percent=${root_inode_percent} docker_inode_percent=${docker_inode_percent} five_xx_5m=${five_xx_count} ssl_min_days=${minimum_ssl_days} log_bytes=${log_bytes} network_bytes='${network_bytes}' uptime_seconds=${uptime_seconds}"

exit 0

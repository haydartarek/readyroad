#!/usr/bin/env bash

set -Eeuo pipefail

readonly BACKUP_ROOT="${BACKUP_ROOT:-/var/lib/docker/volumes/readyroad-backups/_data}"
readonly BACKUP_STATE_DIR="${BACKUP_STATE_DIR:-/var/lib/readyroad-backup/state}"
readonly AGE_RECIPIENT_FILE="${AGE_RECIPIENT_FILE:-/etc/readyroad-backup/age-recipient.txt}"

umask 077

log_event() {
  local level="$1"
  local event="$2"
  shift 2

  logger --tag readyroad-backup -- \
    "level=${level} event=${event} $*"
  printf 'level=%s event=%s %s\n' "$level" "$event" "$*"
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    log_event CRITICAL missing_command "command=$1"
    return 1
  }
}

require_file() {
  [[ -s "$1" ]] || {
    log_event CRITICAL missing_file "path=$1"
    return 1
  }
}

ensure_backup_layout() {
  mkdir -p \
    "${BACKUP_ROOT}/db/daily" \
    "${BACKUP_ROOT}/db/weekly" \
    "${BACKUP_ROOT}/db/monthly" \
    "${BACKUP_ROOT}/config/weekly" \
    "${BACKUP_ROOT}/config/deployment" \
    "${BACKUP_ROOT}/.staging" \
    "$BACKUP_STATE_DIR"
  chmod 0700 "$BACKUP_ROOT" "${BACKUP_ROOT}/.staging" "$BACKUP_STATE_DIR"
}

read_age_recipient() {
  local recipient

  require_file "$AGE_RECIPIENT_FILE"
  recipient="$(head -n 1 "$AGE_RECIPIENT_FILE" | tr -d '\r\n')"
  [[ "$recipient" =~ ^age1[0-9a-z]+$ ]] || {
    log_event CRITICAL invalid_age_recipient \
      "path=${AGE_RECIPIENT_FILE}"
    return 1
  }
  printf '%s' "$recipient"
}

atomic_write() {
  local destination="$1"
  local content="$2"
  local temporary="${destination}.partial.$$"

  printf '%s\n' "$content" >"$temporary"
  chmod 0600 "$temporary"
  mv -f "$temporary" "$destination"
}

encrypt_artifact() {
  local plaintext="$1"
  local encrypted="$2"
  local metadata_type="$3"
  local timestamp="$4"
  local recipient encrypted_partial
  local plaintext_hash encrypted_hash
  local plaintext_name encrypted_name

  require_file "$plaintext"
  recipient="$(read_age_recipient)"
  encrypted_partial="${encrypted}.partial"
  plaintext_name="$(basename "$plaintext")"
  encrypted_name="$(basename "$encrypted")"

  plaintext_hash="$(sha256sum "$plaintext" | awk '{print $1}')"
  age --recipient "$recipient" --output "$encrypted_partial" "$plaintext"
  chmod 0600 "$encrypted_partial"
  mv -f "$encrypted_partial" "$encrypted"
  encrypted_hash="$(sha256sum "$encrypted" | awk '{print $1}')"

  atomic_write "${encrypted}.sha256" \
    "${encrypted_hash}  ${encrypted_name}"
  atomic_write "${encrypted%.age}.sha256" \
    "${plaintext_hash}  ${plaintext_name}"
  atomic_write "${encrypted%.age}.meta" \
    "type=${metadata_type}
created_at=${timestamp}
host=$(hostname)
encrypted_file=${encrypted_name}
plaintext_file=${plaintext_name}
encrypted_bytes=$(stat -c %s "$encrypted")
plaintext_bytes=$(stat -c %s "$plaintext")
encryption=age-x25519"
}

copy_artifact_set() {
  local encrypted="$1"
  local destination="$2"
  local base

  mkdir -p "$destination"
  base="$(basename "$encrypted")"
  cp -p --reflink=auto "$encrypted" "${destination}/${base}"
  cp -p --reflink=auto "${encrypted}.sha256" \
    "${destination}/${base}.sha256"
  cp -p --reflink=auto "${encrypted%.age}.sha256" \
    "${destination}/${base%.age}.sha256"
  cp -p --reflink=auto "${encrypted%.age}.meta" \
    "${destination}/${base%.age}.meta"
}

prune_artifacts() {
  local directory="$1"
  local prefix="$2"
  local keep="$3"
  local mode="${4:-execute}"
  local index=0 encrypted base
  local -a encrypted_files=()

  [[ "$keep" =~ ^[1-9][0-9]*$ ]] || {
    log_event CRITICAL invalid_retention "keep=$keep"
    return 1
  }
  [[ "$mode" == "execute" || "$mode" == "dry-run" ]] || return 1
  [[ -d "$directory" ]] || return 0

  mapfile -t encrypted_files < <(
    find "$directory" -maxdepth 1 -type f \
      -name "${prefix}*.age" -printf '%f\n' |
      sort -r
  )

  for encrypted in "${encrypted_files[@]}"; do
    index=$((index + 1))
    if (( index <= keep )); then
      log_event INFO retention_keep \
        "mode=${mode} file=${encrypted} position=${index} keep=${keep}"
      continue
    fi

    base="${encrypted%.age}"
    log_event INFO retention_delete \
      "mode=${mode} file=${encrypted} position=${index} keep=${keep}"
    if [[ "$mode" == "execute" ]]; then
      rm -f -- \
        "${directory}/${encrypted}" \
        "${directory}/${encrypted}.sha256" \
        "${directory}/${base}.sha256" \
        "${directory}/${base}.meta"
    fi
  done
}

check_free_space() {
  local minimum_kb="${1:-2097152}"
  local available_kb

  available_kb="$(df -Pk "$BACKUP_ROOT" | awk 'NR == 2 {print $4}')"
  [[ "$available_kb" =~ ^[0-9]+$ ]] || return 1
  if (( available_kb < minimum_kb )); then
    log_event CRITICAL insufficient_disk \
      "available_kb=${available_kb} required_kb=${minimum_kb}"
    return 1
  fi
}

verify_encrypted_checksum() {
  local encrypted="$1"

  (
    cd "$(dirname "$encrypted")"
    sha256sum --check --status "$(basename "${encrypted}.sha256")"
  )
}

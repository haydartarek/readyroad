#!/usr/bin/env bash

set -Eeuo pipefail

readonly DEPLOY_SCRIPT="/opt/readyroad/bin/readyroad-deploy"
readonly ORIGINAL_COMMAND="${SSH_ORIGINAL_COMMAND:-}"

reject() {
  printf 'Rejected GitHub Actions deployment command.\n' >&2
  exit 64
}

read -r -a fields <<<"$ORIGINAL_COMMAND"

(( ${#fields[@]} == 4 )) || reject
[[ "${fields[0]}" == "deploy" ]] || reject
[[ "${fields[1]}" =~ ^[0-9a-f]{40}$ ]] || reject
[[ "${fields[2]}" =~ ^[0-9a-f]{40}$ ]] || reject
[[ "${fields[3]}" =~ ^[A-Za-z0-9][A-Za-z0-9._-]{0,79}$ ]] || reject
[[ -x "$DEPLOY_SCRIPT" ]] || {
  printf 'Gate 5D deploy command is unavailable.\n' >&2
  exit 69
}

exec sudo -n "$DEPLOY_SCRIPT" \
  --backend-ref "${fields[1]}" \
  --frontend-ref "${fields[2]}" \
  --release-id "${fields[3]}"

#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
if [[ -r /opt/readyroad/bin/readyroad-deploy-lib ]]; then
  # shellcheck source=/dev/null
  source /opt/readyroad/bin/readyroad-deploy-lib
else
  # shellcheck source=deploy-lib.sh
  source "${SCRIPT_DIR}/deploy-lib.sh"
fi

FRONTEND_URL="${READYROAD_FRONTEND_URL:-https://rijvia.be}"
API_URL="${READYROAD_API_URL:-https://api.rijvia.be}"
ENV_FILE="${READYROAD_CURRENT_LINK}/.env.production"
temporary_directory=""
checks=0
umask 077

usage() {
  printf 'Usage: %s [--env-file PATH] [--frontend-url URL] [--api-url URL]\n' \
    "$0" >&2
  exit 2
}

while (( $# > 0 )); do
  case "$1" in
    --env-file)
      [[ $# -ge 2 ]] || usage
      ENV_FILE="$2"
      shift 2
      ;;
    --frontend-url)
      [[ $# -ge 2 ]] || usage
      FRONTEND_URL="$2"
      shift 2
      ;;
    --api-url)
      [[ $# -ge 2 ]] || usage
      API_URL="$2"
      shift 2
      ;;
    *)
      usage
      ;;
  esac
done

[[ -s "$ENV_FILE" ]] || rr_die "smoke_env_missing"
rr_require_commands curl jq mktemp

temporary_directory="$(mktemp -d /run/readyroad-smoke.XXXXXX)"
chmod 0700 "$temporary_directory"
cleanup() {
  rm -rf -- "$temporary_directory"
}
trap cleanup EXIT

probe_get() {
  local name="$1"
  local url="$2"
  local jq_filter="${3:-}"
  local output="${temporary_directory}/${name}.json"
  local status

  status="$(curl --silent --show-error --location --max-time 90 \
    --output "$output" --write-out '%{http_code}' "$url")"
  [[ "$status" == "200" ]] ||
    rr_die "smoke_http_failure:${name}:${status}"
  if [[ -n "$jq_filter" ]]; then
    jq --exit-status "$jq_filter" "$output" >/dev/null ||
      rr_die "smoke_payload_failure:${name}"
  fi
  checks=$((checks + 1))
  rr_log INFO smoke_check "name=${name} status=passed"
}

probe_authenticated() {
  local name="$1"
  local url="$2"
  local auth_config="$3"
  local output="${temporary_directory}/${name}.json"
  local status

  status="$(curl --silent --show-error --max-time 90 \
    --config "$auth_config" \
    --output "$output" --write-out '%{http_code}' "$url")"
  [[ "$status" == "200" ]] ||
    rr_die "smoke_http_failure:${name}:${status}"
  checks=$((checks + 1))
  rr_log INFO smoke_check "name=${name} status=passed"
}

probe_get frontend "${FRONTEND_URL}/"
probe_get backend_health "${API_URL}/actuator/health" '.status == "UP"'
probe_get traffic_signs "${API_URL}/api/traffic-signs" \
  'if type == "array" then length > 0 else (.content | length) > 0 end'
probe_get lessons "${API_URL}/api/lessons" \
  'if type == "array" then length > 0 else (.content | length) > 0 end'
probe_get random_quiz "${API_URL}/api/quiz/random?count=5" \
  'type == "array" and length == 5'
probe_get robots "${FRONTEND_URL}/robots.txt"
probe_get sitemap "${FRONTEND_URL}/sitemap.xml"

admin_password="$(
  sed -n 's/^ADMIN_DEFAULT_PASSWORD=//p' "$ENV_FILE" |
    tail -n 1 |
    tr -d '\r'
)"
[[ -n "$admin_password" ]] || rr_die "smoke_admin_password_missing"
login_request="${temporary_directory}/login-request.json"
jq --null-input --compact-output \
  --arg username admin --arg password "$admin_password" \
  '{username:$username,password:$password}' >"$login_request"
login_output="${temporary_directory}/login.json"
login_status="$(curl --silent --show-error --max-time 90 \
  --request POST --header 'Content-Type: application/json' \
  --data-binary "@${login_request}" --output "$login_output" \
  --write-out '%{http_code}' "${API_URL}/api/auth/login")"
admin_password=""
rm -f "$login_request"
[[ "$login_status" == "200" ]] ||
  rr_die "smoke_http_failure:login:${login_status}"
token="$(jq --exit-status --raw-output \
  '.token | select(type == "string" and length > 20)' "$login_output")"
checks=$((checks + 1))
rr_log INFO smoke_check "name=login status=passed"

auth_config="${temporary_directory}/auth.curl"
printf 'header = "Authorization: Bearer %s"\n' "$token" >"$auth_config"
chmod 0600 "$auth_config"
token=""

probe_authenticated auth_me "${API_URL}/api/auth/me" "$auth_config"
probe_authenticated admin_authorization \
  "${API_URL}/api/admin/dashboard" "$auth_config"
probe_authenticated user_progress \
  "${API_URL}/api/sign-quiz/user-progress" "$auth_config"

rr_log INFO smoke_completed "checks=${checks} status=passed"

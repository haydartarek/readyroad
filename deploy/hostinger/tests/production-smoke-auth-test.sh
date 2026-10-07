#!/usr/bin/env bash
set -Eeuo pipefail

TEST_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SMOKE_SCRIPT="${TEST_DIR}/../scripts/production-smoke.sh"
temporary_directory="$(mktemp -d /tmp/readyroad-smoke-auth-test.XXXXXX)"
trap 'rm -rf -- "$temporary_directory"' EXIT
checks=0

rr_die() { printf '%s\n' "$1" >&2; exit 1; }
rr_log() { :; }

# Exercise the actual probe function without contacting production.
eval "$(awk '/^probe_get\(\) \{/ {capture=1} capture {print} capture && /^\}$/ {exit}' "$SMOKE_SCRIPT")"

auth_config="${temporary_directory}/auth.curl"
printf 'header = "Authorization: Bearer smoke-test-token"\n' >"$auth_config"
chmod 0600 "$auth_config"
fixture='[{},{},{},{},{}]'
curl() {
  local output="" config=""
  while (( $# )); do
    case "$1" in
      --output) output="$2"; shift 2 ;;
      --config) config="$2"; shift 2 ;;
      --max-time|--write-out) shift 2 ;;
      *) shift ;;
    esac
  done
  if [[ "$config" == "$auth_config" && -s "$config" ]]; then
    printf '%s' "$fixture" >"$output"
    printf '200'
  else
    printf '{"message":"Unauthorized"}' >"$output"
    printf '401'
  fi
}

probe_get random_quiz "https://example.test/api/quiz/random?count=5" \
  'type == "array" and length == 5' "$auth_config"
[[ "$checks" -eq 1 ]] || rr_die "authenticated_probe_not_counted"

if (probe_get random_quiz "https://example.test/api/quiz/random?count=5" \
    'type == "array" and length == 5') 2>/dev/null; then
  rr_die "anonymous_failure_was_ignored"
fi

fixture='[{},{},{},{}]'
if (probe_get random_quiz "https://example.test/api/quiz/random?count=5" \
    'type == "array" and length == 5' "$auth_config") 2>/dev/null; then
  rr_die "invalid_question_count_was_ignored"
fi

auth_line="$(awk '/^auth_config=/ {print NR}' "$SMOKE_SCRIPT")"
quiz_line="$(awk '/^probe_get random_quiz / {print NR}' "$SMOKE_SCRIPT")"
[[ "$auth_line" -lt "$quiz_line" ]] || rr_die "quiz_probe_precedes_authentication"
printf 'PASS: authenticated quiz, anonymous rejection, payload validation, authentication order\n'

#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
TEST_ROOT="$(mktemp -d /tmp/readyroad-deploy-test.XXXXXX)"
readonly TEST_ROOT
export READYROAD_ROOT="$TEST_ROOT"
export READYROAD_RELEASES_DIR="${TEST_ROOT}/releases"
export READYROAD_CURRENT_LINK="${TEST_ROOT}/current"
export READYROAD_STATE_DIR="${TEST_ROOT}/rollback-state"
export READYROAD_LOG_DIR="${TEST_ROOT}/logs"

# shellcheck source-path=SCRIPTDIR
# shellcheck source=../scripts/deploy-lib.sh
source "${SCRIPT_DIR}/../scripts/deploy-lib.sh"

cleanup() {
  rm -rf --one-file-system "$TEST_ROOT"
}
trap cleanup EXIT

fail() {
  printf 'FAILED: %s\n' "$1" >&2
  exit 1
}

make_release() {
  local name="$1"
  local status="${2:-SUCCESS}"

  install -d -m 0700 "${READYROAD_RELEASES_DIR}/${name}"
  printf 'DEPLOYMENT_STATUS=%s\n' "$status" \
    >"${READYROAD_RELEASES_DIR}/${name}/release-manifest.env"
}

install -d -m 0700 "$READYROAD_RELEASES_DIR"
for suffix in 01 02 03 04 05 06 07 08 09; do
  make_release "20260724-${suffix}"
done
make_release "20260724-10-failed" FAILED

current_release="${READYROAD_RELEASES_DIR}/20260724-02"
previous_release="${READYROAD_RELEASES_DIR}/20260724-01"
rr_atomic_current_link "$current_release"
[[ "$(readlink -f "$READYROAD_CURRENT_LINK")" == "$current_release" ]] ||
  fail "atomic current symlink"

before_dry_run="$(find "$READYROAD_RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d | wc -l)"
rr_prune_releases dry-run 5 "$current_release" "$previous_release"
after_dry_run="$(find "$READYROAD_RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d | wc -l)"
[[ "$before_dry_run" == "$after_dry_run" ]] ||
  fail "dry-run changed releases"

rr_prune_releases execute 5 "$current_release" "$previous_release"

for retained in 01 02 05 06 07 08 09; do
  [[ -d "${READYROAD_RELEASES_DIR}/20260724-${retained}" ]] ||
    fail "expected retained release ${retained}"
done
[[ -d "${READYROAD_RELEASES_DIR}/20260724-10-failed" ]] ||
  fail "failed release was removed"
for removed in 03 04; do
  [[ ! -e "${READYROAD_RELEASES_DIR}/20260724-${removed}" ]] ||
    fail "eligible release ${removed} was retained"
done

# Exercise activation with synthetic configuration only; never read production env.
activation_current="${TEST_ROOT}/activation-current"
activation_next="${TEST_ROOT}/activation-next"
mkdir -p "$activation_current" "$activation_next"
image_id="sha256:$(printf '%064d' 1)"
printf 'BACKEND_IMAGE_ID=%s\n' "$image_id" \
  >"${activation_next}/release-manifest.env"
fixture_running_state="${image_id} true healthy"
activation_calls="${TEST_ROOT}/activation-calls"
cat >"${activation_current}/config.json" <<'EOF'
{"services":{"backend":{"image":"readyroad-backend:old","build":{"context":"/old/backend"},"environment":{"BACKEND_IMAGE_TAG":"old","FRONTEND_IMAGE_TAG":"old","PORT":"8890","SPRING_PROFILES_ACTIVE":"secure,postgresql"},"volumes":[{"type":"volume","source":"readyroad-uploads","target":"/app/public/images/quiz"}],"healthcheck":{"test":["CMD","wget","/actuator/health"]}}}}
EOF
jq '.services.backend.image="readyroad-backend:new" |
  .services.backend.build.context="/new/backend" |
  .services.backend.environment.BACKEND_IMAGE_TAG="new" |
  .services.backend.environment.FRONTEND_IMAGE_TAG="new"' \
  "${activation_current}/config.json" >"${activation_next}/config.json"
cp "${activation_next}/config.json" "${activation_next}/baseline.json"

docker() {
  [[ "$1" == inspect ]] || fail "unexpected Docker command"
  printf '%s\n' "$fixture_running_state"
}
rr_compose() {
  local release="$1"
  shift
  if [[ "$1" == config ]]; then
    cat "${release}/config.json"
  else
    printf '%s\n' "$*" >>"$activation_calls"
  fi
}
assert_activation() {
  local expected="$1"
  : >"$activation_calls"
  rr_activate_application "$activation_current" "$activation_next"
  [[ "$(cat "$activation_calls")" == "$expected" ]] ||
    fail "unexpected activation: $expected"
}

frontend_only="up -d --no-deps --no-build frontend"
both_services="up -d --no-build backend frontend"
assert_activation "$frontend_only"
fixture_running_state="sha256:$(printf '%064d' 2) true healthy"
assert_activation "$both_services"
fixture_running_state="${image_id} true unhealthy"
assert_activation "$both_services"
fixture_running_state="${image_id} false healthy"
assert_activation "$both_services"
fixture_running_state="${image_id} true healthy"
for mutation in \
  '.services.backend.environment.PORT="8891"' \
  '.services.backend.volumes[0].source="different-uploads"' \
  '.services.backend.healthcheck.test=["CMD","different-probe"]'; do
  jq "$mutation" "${activation_next}/baseline.json" \
    >"${activation_next}/config.json"
  assert_activation "$both_services"
done
printf 'invalid-json\n' >"${activation_next}/config.json"
: >"$activation_calls"
set +e
( set -e; rr_activate_application "$activation_current" "$activation_next" ) \
  >/dev/null 2>&1
invalid_status=$?
set -e
[[ "$invalid_status" -ne 0 && ! -s "$activation_calls" ]] ||
  fail "invalid config must abort before activation"

printf 'deployment_activation_tests=8_passed\n'
printf 'deployment_automation_unit_test=PASSED\n'

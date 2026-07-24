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

printf 'deployment_automation_unit_test=PASSED\n'

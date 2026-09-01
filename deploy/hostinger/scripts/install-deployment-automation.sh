#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR
DEPLOY_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
readonly DEPLOY_ROOT
readonly TARGET_DIR="/opt/readyroad/bin"

[[ "$EUID" -eq 0 ]] || {
  printf 'Installation must run as root.\n' >&2
  exit 1
}

install -d -o root -g root -m 0755 "$TARGET_DIR"
install -d -o root -g root -m 0755 "${TARGET_DIR}/templates"
install -o root -g root -m 0644 \
  "${SCRIPT_DIR}/deploy-lib.sh" \
  "${TARGET_DIR}/readyroad-deploy-lib"
install -o root -g root -m 0755 \
  "${SCRIPT_DIR}/deploy-production.sh" \
  "${TARGET_DIR}/readyroad-deploy"
install -o root -g root -m 0755 \
  "${SCRIPT_DIR}/github-actions-deploy-command.sh" \
  "${TARGET_DIR}/readyroad-ci-command"
install -o root -g root -m 0755 \
  "${SCRIPT_DIR}/rollback-release.sh" \
  "${TARGET_DIR}/readyroad-rollback"
install -o root -g root -m 0755 \
  "${SCRIPT_DIR}/production-smoke.sh" \
  "${TARGET_DIR}/readyroad-smoke"
install -o root -g root -m 0644 \
  "${DEPLOY_ROOT}/docker-compose.yml" \
  "${TARGET_DIR}/templates/docker-compose.yml"
install -o root -g root -m 0644 \
  "${DEPLOY_ROOT}/Caddyfile.production" \
  "${TARGET_DIR}/templates/Caddyfile.production"

if ! id readyroad-ci >/dev/null 2>&1; then
  useradd --create-home --shell /bin/bash readyroad-ci
fi
install -d -o readyroad-ci -g readyroad-ci -m 0700 \
  /home/readyroad-ci/.ssh
install -o root -g root -m 0440 \
  "${DEPLOY_ROOT}/readyroad-ci.sudoers" \
  /etc/sudoers.d/readyroad-ci
visudo -cf /etc/sudoers.d/readyroad-ci >/dev/null

printf 'ReadyRoad deployment automation installed in %s\n' "$TARGET_DIR"

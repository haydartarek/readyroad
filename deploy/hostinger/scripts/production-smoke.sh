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
rr_require_commands curl jq mktemp docker find sort grep head tail

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

probe_published_articles() {
  local language prefix catalog slug

  for language in EN AR NL FR; do
    prefix="/${language,,}"
    [[ "$language" != "EN" ]] || prefix=""
    probe_get "blog_${language}" "${FRONTEND_URL}${prefix}/blog"
    probe_get "article_catalog_${language}" "${API_URL}/api/articles?language=${language}" \
      'type == "array" and all(.[]; .slug | type == "string" and length > 0)'
    catalog="${temporary_directory}/article_catalog_${language}.json"
    if [[ "$(jq 'length' "$catalog")" == "0" ]]; then
      rr_log INFO article_route_smoke "language=${language} result=no_published_articles"
      continue
    fi

    slug="$(jq --exit-status --raw-output '.[0].slug | @uri' "$catalog")"
    probe_get "published_article_${language}" "${FRONTEND_URL}${prefix}/blog/${slug}"
  done
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

probe_theory_image_integrity() {
  local sql_file="${temporary_directory}/theory-image-integrity.sql"
  local query_output="${temporary_directory}/theory-image-integrity.out"
  local db_links="${temporary_directory}/theory-image-links.txt"
  local disk_files="${temporary_directory}/theory-image-files.txt"
  local upload_mount counts_line total_questions linked_questions
  local invalid_link_questions required_missing_questions
  local missing_file_questions=0
  local valid_image_questions=0
  local orphan_uploads=0
  local filename

  upload_mount="$(
    docker volume inspect readyroad-uploads \
      --format '{{.Mountpoint}}'
  )"

  [[ -n "$upload_mount" && -d "$upload_mount" ]] ||
    rr_die "smoke_theory_upload_volume_missing"

  cat >"$sql_file" <<'SQL'
SELECT
    COUNT(*)::text || '|' ||
    COUNT(*) FILTER (
        WHERE content_image_url ~*
            '^/images/quiz/[A-Za-z0-9][A-Za-z0-9._-]*\.(jpg|jpeg|png|webp)$'
    )::text || '|' ||
    COUNT(*) FILTER (
        WHERE NULLIF(BTRIM(content_image_url), '') IS NOT NULL
          AND content_image_url !~*
            '^/images/quiz/[A-Za-z0-9][A-Za-z0-9._-]*\.(jpg|jpeg|png|webp)$'
    )::text || '|' ||
    COUNT(*) FILTER (
        WHERE (
            question_type = 'IMAGE_BASED'
            OR COALESCE(requires_sign_image, false)
        )
        AND (
            NULLIF(BTRIM(content_image_url), '') IS NULL
            OR content_image_url !~*
              '^/images/quiz/[A-Za-z0-9][A-Za-z0-9._-]*\.(jpg|jpeg|png|webp)$'
        )
    )::text
FROM quiz_questions;

SELECT regexp_replace(
    content_image_url,
    '^/images/quiz/',
    ''
)
FROM quiz_questions
WHERE content_image_url ~*
    '^/images/quiz/[A-Za-z0-9][A-Za-z0-9._-]*\.(jpg|jpeg|png|webp)$'
ORDER BY id;
SQL

  docker run --rm -i \
    --network "$READYROAD_NETWORK" \
    --env-file "$ENV_FILE" \
    --volume "${sql_file}:/query.sql:ro" \
    postgres:16-alpine \
    sh -ceu '
      DB="${POSTGRES_DATABASE:-${POSTGRES_DB:-readyroad_postgresql}}"
      PORT="${POSTGRES_PORT:-5433}"
      SCHEMA="${POSTGRES_SCHEMA:-readyroad}"
      SSLMODE="${POSTGRES_SSL_MODE:-${POSTGRES_SSLMODE:-disable}}"

      case "$SCHEMA" in
        *[!A-Za-z0-9_]*|"")
          exit 2
          ;;
      esac

      export PGPASSWORD="$POSTGRES_PASSWORD"
      export PGOPTIONS="-c search_path=$SCHEMA,public"

      exec psql \
        "host=$POSTGRES_HOST port=$PORT dbname=$DB user=$POSTGRES_USERNAME sslmode=$SSLMODE" \
        -qAt \
        -v ON_ERROR_STOP=1 \
        -f /query.sql
    ' >"$query_output"

  counts_line="$(head -n 1 "$query_output")"

  [[ "$counts_line" =~ ^[0-9]+\|[0-9]+\|[0-9]+\|[0-9]+$ ]] ||
    rr_die "smoke_theory_image_counts_invalid"

  IFS='|' read -r \
    total_questions \
    linked_questions \
    invalid_link_questions \
    required_missing_questions <<<"$counts_line"

  (( total_questions > 0 )) ||
    rr_die "smoke_theory_image_bank_empty"

  (( invalid_link_questions == 0 )) ||
    rr_die \
      "smoke_theory_image_invalid_links:count=${invalid_link_questions}"

  (( required_missing_questions == 0 )) ||
    rr_die \
      "smoke_theory_required_image_gap:count=${required_missing_questions}"

  tail -n +2 "$query_output" >"$db_links"

  find "$upload_mount" \
    -maxdepth 1 \
    -type f \
    -printf '%f\n' \
    | sort >"$disk_files"

  while IFS= read -r filename; do
    [[ -n "$filename" ]] || continue

    if [[ ! -f "${upload_mount}/${filename}" ]]; then
      missing_file_questions=$((missing_file_questions + 1))
    fi
  done <"$db_links"

  valid_image_questions=$((linked_questions - missing_file_questions))

  if (( linked_questions != valid_image_questions )); then
    rr_die \
      "smoke_theory_image_integrity_failed:linked=${linked_questions}:valid=${valid_image_questions}:missing_files=${missing_file_questions}"
  fi

  while IFS= read -r filename; do
    [[ -n "$filename" ]] || continue

    if ! grep -Fxq -- "$filename" "$db_links"; then
      orphan_uploads=$((orphan_uploads + 1))
    fi
  done <"$disk_files"

  checks=$((checks + 1))

  rr_log INFO smoke_check \
    "name=theory_image_integrity status=passed total_questions=${total_questions} image_linked_questions=${linked_questions} questions_with_valid_images=${valid_image_questions} required_image_gaps=${required_missing_questions} invalid_image_links=${invalid_link_questions} orphan_uploads=${orphan_uploads} orphan_policy=ignored"
}

probe_theory_image_integrity

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
probe_published_articles

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

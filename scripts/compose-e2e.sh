#!/usr/bin/env bash
set -euo pipefail

# Runs against the Compose development stack. Start it with:
# docker compose --env-file .env.example up --build --wait
api_url="${API_URL:-http://localhost:8080/api}"
keycloak_url="${KEYCLOAK_URL:-http://localhost:8081}"
realm="${KEYCLOAK_REALM:-adr-manager}"

token() {
  curl --fail --silent --show-error \
    --data "grant_type=password" --data "client_id=adr-manager-spa" \
    --data "username=$1" --data "password=$2" \
    "$keycloak_url/realms/$realm/protocol/openid-connect/token" | jq -er '.access_token'
}

jwt_subject() {
  cut -d. -f2 <<<"$1" | tr '_-' '/+' | base64 -d 2>/dev/null | jq -er '.sub'
}

request() {
  local method="$1" path="$2" access_token="$3" body="${4:-}"
  if [[ -n "$body" ]]; then
    curl --fail --silent --show-error -X "$method" "$api_url$path" \
      -H "Authorization: Bearer $access_token" -H 'Content-Type: application/json' --data "$body"
  else
    curl --fail --silent --show-error -X "$method" "$api_url$path" -H "Authorization: Bearer $access_token"
  fi
}

user_token="$(token dave.user dave-password)"
user_id="$(jwt_subject "$user_token")"
suffix="$(date +%s)"

adl="$(request POST /adls "$user_token" "{\"title\":\"E2E ADL $suffix\",\"context\":\"Validate local stack\",\"problem\":\"Confirm a full decision workflow\",\"tags\":[\"e2e\"]}")"
adl_identifier="$(jq -er '.identifier' <<<"$adl")"

adr="$(request POST "/adls/$adl_identifier/adrs" "$user_token" "{\"title\":\"E2E ADR $suffix\",\"context\":\"Validate local stack\",\"problem\":\"Confirm a full decision workflow\",\"optionsConsidered\":\"Use the Compose stack\",\"decision\":\"Use the Compose stack\",\"consequences\":\"The scenario is repeatable\"}")"
adr_identifier="$(jq -er '.identifier' <<<"$adr")"
adr_version="$(jq -er '.version' <<<"$adr")"

submitted="$(request POST "/adrs/$adr_identifier/submit" "$user_token" "{\"version\":$adr_version}")"
submitted_version="$(jq -er '.version' <<<"$submitted")"
approved="$(request POST "/adrs/$adr_identifier/approve" "$user_token" "{\"version\":$submitted_version,\"comment\":\"Approved by Compose E2E\"}")"
jq -e '.status == "APPROVED"' <<<"$approved" >/dev/null

request GET "/adls/$adl_identifier" "$user_token" |
  jq -e --arg id "$adr_identifier" '.adrs | any(.identifier == $id and .status == "APPROVED")' >/dev/null

request GET "/adls/$adl_identifier/audit-events" "$user_token" |
  jq -e 'map(.action) | index("CREATED")' >/dev/null
request GET "/adrs/$adr_identifier/audit-events" "$user_token" |
  jq -e 'map(.action) | index("CREATED") and index("SUBMITTED") and index("APPROVED")' >/dev/null

printf 'Compose E2E scenario passed: %s / %s\n' "$adl_identifier" "$adr_identifier"

#!/usr/bin/env bash
# BANK S-42 — Rollback drill.
#
# Validates the documented rollback procedure (deploy/argocd/README.md
# §Rollback) against a live ArgoCD Application — typically the staging
# Application, never production. Idempotent: re-running the drill
# leaves the target Application synced + healthy at its current revision.
#
# Drill steps:
#   1. argocd app get      — capture the current Synced revision.
#   2. argocd app history  — pick the immediate-prior revision N-1.
#   3. argocd app rollback — roll to N-1.
#   4. wait                — assert Synced/Healthy within timeout.
#   5. argocd app sync     — restore to head, re-asserting.
#
# Usage:
#   deploy/argocd/rollback-drill.sh <application-name>
#
# Required tools: argocd, jq. Authentication must already be established
# (env: ARGOCD_SERVER + ARGOCD_AUTH_TOKEN, or a prior `argocd login`).
set -euo pipefail

APP="${1:-}"
TIMEOUT="${TIMEOUT:-300}"  # seconds

if [[ -z "${APP}" ]]; then
  echo "usage: rollback-drill.sh <application-name>" >&2
  exit 2
fi

if [[ "${APP}" == *production* ]]; then
  echo "rollback-drill.sh: refusing to run against a production Application." >&2
  echo "  Production rollbacks are documented but executed by hand by a Release Manager." >&2
  exit 3
fi

for tool in argocd jq; do
  command -v "${tool}" >/dev/null || { echo "rollback-drill.sh: missing dep: ${tool}" >&2; exit 4; }
done

log() { printf '[rollback-drill %s] %s\n' "$(date -u +%H:%M:%S)" "$*"; }

log "step 1: capturing current revision of ${APP}"
HEAD_REV="$(argocd app get "${APP}" -o json | jq -r '.status.sync.revision')"
log "  current revision = ${HEAD_REV}"

log "step 2: locating immediate-prior history entry"
PREV_ID="$(argocd app history "${APP}" -o json \
  | jq -r 'sort_by(.id) | .[-2].id // empty')"
if [[ -z "${PREV_ID}" ]]; then
  log "ABORT: ${APP} has fewer than 2 history entries — nothing to roll back to."
  exit 5
fi
log "  prior history id = ${PREV_ID}"

log "step 3: rolling ${APP} back to history id ${PREV_ID}"
argocd app rollback "${APP}" "${PREV_ID}" --prune

log "step 4: waiting up to ${TIMEOUT}s for Synced/Healthy"
argocd app wait "${APP}" --sync --health --timeout "${TIMEOUT}"

log "step 5: re-syncing to head so the drill leaves no drift"
argocd app sync "${APP}" --revision "${HEAD_REV}" --prune
argocd app wait "${APP}" --sync --health --timeout "${TIMEOUT}"

log "drill complete: ${APP} restored to ${HEAD_REV}"

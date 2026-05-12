# ArgoCD deployment topology (S-42)

GitOps wiring for the `teller` Helm chart. Two environments, one manifest per
environment, regenerated from the chart by `generate.sh`.

| File                                | Environment | Sync policy           | Cluster            | Namespace         |
| ----------------------------------- | ----------- | --------------------- | ------------------ | ----------------- |
| `appproject.yaml`                   | scope       | n/a                   | `argocd`           | `argocd`          |
| `application-staging.yaml`          | staging     | automated (prune+heal)| `microk8s-staging` | `bank-staging`    |
| `application-production.yaml`       | production  | manual approval gate  | `eks-production`   | `bank-production` |

## Promotion flow

```
PR → main      (ci.yml + integration-tests.yml + build-images.yml)
      │
      ▼
build-images.yml pushes ghcr.io/edgentx/teller-{core,frontend}:sha-<short>
      │
      ▼
promote-image.yml (separate PR) bumps backend.image.tag in
deploy/helm/teller/values-microk8s.yaml  →  ArgoCD auto-syncs staging.
      │
      ▼  (operator validates staging)
      │
promote-image.yml bumps the same tag in values-eks.yaml  →  ArgoCD shows
"OutOfSync" on teller-production  →  Release Manager clicks Sync.
```

## Manifest generation

`application-{staging,production}.yaml` are **generated** from `Chart.yaml` and
the per-environment values overlays. Edit the chart, not the manifests:

```bash
./deploy/argocd/generate.sh           # rewrite the two YAMLs
./deploy/argocd/generate.sh --check   # CI mode — fails if drift exists
```

CI (`.github/workflows/ci.yml` → `lint` job) runs `--check` on every PR so a
chart change can never land without its matching ArgoCD manifest update.

## Rollback procedure

Every rollback is **a Git operation followed by an ArgoCD operation**. There is
no `kubectl rollout undo` step — that would break the GitOps invariant that
cluster state matches `main`.

### Fast path (one bad deploy, < 5 min ago)

```bash
# 1. Identify the bad sync (last ArgoCD revision before the failing one)
argocd app history teller-production
# ID  REVISION                                                         DEPLOYED AT
# 12  abc1234 (good)                                                   2026-05-12T14:00:00Z
# 13  def5678 (bad)                                                    2026-05-12T14:05:00Z

# 2. Roll the Application back to revision 12. ArgoCD re-applies the chart
#    at that Git SHA against the live cluster — Deployments, ConfigMaps,
#    Ingresses all snap to their previous state.
argocd app rollback teller-production 12

# 3. Verify health
argocd app get teller-production
kubectl -n bank-production rollout status deploy/teller-core
```

`revisionHistoryLimit: 50` on production means rollback can reach back about a
sprint of deploys (vs. 20 on staging).

### Durable path (publish a revert commit)

`argocd app rollback` is a *cluster* operation — it does NOT change `main`. To
keep GitOps honest, immediately follow up with a Git revert so the next
auto-sync doesn't replay the bad change:

```bash
git revert <bad-sha>
git push origin main
gh pr create --title "revert: rollback teller-production to <good-sha>" \
             --body "Reverting #<bad-pr>; staging green, prod rolled back via argocd app rollback teller-production <id>."
```

### Image-only rollback (chart unchanged, only the tag is bad)

If only `backend.image.tag` is wrong (e.g. a Trivy regression slipped through):

```bash
# Edit deploy/helm/teller/values-eks.yaml, set image.tag back to the
# previous sha-XXXXXXXX value, commit on a hot-fix branch.
$EDITOR deploy/helm/teller/values-eks.yaml
git commit -am "fix(prod): pin teller-core to sha-abcdef (revert sha-deadbe)"
git push -u origin hotfix/rollback-teller-core
gh pr create --title "[hotfix] rollback teller-core to sha-abcdef" \
             --body "Reason: <CVE / regression / smoke-test failure>"
# Merge → ArgoCD shows OutOfSync → Release Manager clicks Sync.
```

### Disaster path (ArgoCD itself is wedged)

If the ArgoCD application-controller cannot reconcile:

```bash
# 1. Pin the chart at a known-good revision directly (bypasses Argo).
helm upgrade --install teller deploy/helm/teller \
  -n bank-production \
  -f deploy/helm/teller/values.yaml \
  -f deploy/helm/teller/values-eks.yaml \
  --version <good-chart-version>

# 2. Page #bank-deploys, attach `argocd app diff teller-production` output.
# 3. Once Argo is recovered, sync the Application so its tracked revision
#    matches what `helm` just applied. Argo will detect that the live state
#    already matches and mark the app Synced/Healthy.
```

### Rollback drill (tested as part of S-42)

The rollback path is exercised on every release-candidate by running, against
the staging cluster:

```bash
deploy/argocd/rollback-drill.sh teller-staging
```

The drill: capture current revision → trigger a no-op chart bump → roll the
Application back to the captured revision → assert `Synced/Healthy` returns
within 5 minutes. The script is idempotent and safe to run on staging at any
time. See `rollback-drill.sh` for the implementation.

## See also

- `deploy/helm/teller/README.md` — chart layout + `helm template` examples
- `.github/workflows/build-images.yml` (S-40) — registry push
- `.github/workflows/integration-tests.yml` (S-44) — TestContainers IT suite
- `.github/workflows/ci.yml` (S-42) — lint + unit-test stages
- `.github/workflows/notify.yml` (S-42) — Slack status fan-out

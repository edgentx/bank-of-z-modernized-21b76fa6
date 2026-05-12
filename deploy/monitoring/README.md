# Bank-of-Z monitoring stack (S-43)

Prometheus + Grafana + Alertmanager + exporters for the Bank-of-Z
modernization deployment.

## Topology

```
                 ┌───────────────────────────────────────────┐
                 │ namespace: bank-observability             │
                 │                                           │
   teller-core ──┼──▶ Prometheus ──▶ Alertmanager ──▶ Slack  │
   (annotation   │       │                         └─▶ PagerDuty
   discovery)    │       └──▶ Grafana (file-provisioned dashboards)
                 │                                           │
   Redis exporter, node-exporter, kube-state-metrics live    │
   in this same namespace and are auto-discovered.           │
                 └───────────────────────────────────────────┘
```

## Files

| Path                                     | Purpose                                      |
| ---------------------------------------- | -------------------------------------------- |
| `prometheus/prometheus.yaml`             | Scrape config, k8s SD, alert routing target  |
| `prometheus/rules/teller-core.rules.yaml`| App-level alerts (5xx, latency, JVM heap)    |
| `prometheus/rules/infrastructure.rules.yaml` | Redis / MinIO / Temporal / node alerts   |
| `prometheus/deployment.yaml`             | Prometheus Deployment + RBAC + Service       |
| `prometheus/configmap.yaml`              | Placeholder ConfigMaps (filled by kustomize) |
| `alertmanager/alertmanager.yaml`         | Slack + PagerDuty routing, inhibits          |
| `alertmanager/deployment.yaml`           | Alertmanager Deployment + Service            |
| `grafana/deployment.yaml`                | Grafana Deployment + Service                 |
| `grafana/provisioning/datasources/`      | Auto-loaded Prometheus + Alertmanager DS     |
| `grafana/provisioning/dashboards/`       | Dashboard provider config                    |
| `grafana/dashboards/system-health.json`  | HTTP + JVM + container + node health         |
| `grafana/dashboards/business-metrics.json`| Domain-event throughput, cache hit ratios   |
| `grafana/dashboards/temporal-workflows.json`| Temporal workflow / activity / task-queue |
| `exporters/redis-exporter.yaml`          | oliver006/redis_exporter Deployment + Svc    |
| `exporters/node-exporter.yaml`           | prometheus/node-exporter DaemonSet           |
| `exporters/kube-state-metrics.yaml`      | KSM Deployment + RBAC + Service              |
| `exporters/minio.md`                     | Notes on MinIO's native metrics endpoint     |
| `kustomization.yaml`                     | `kubectl apply -k` entry point               |

## Install

```bash
# Validate everything before applying.
kubectl kustomize deploy/monitoring | kubectl apply --dry-run=client -f -

# Apply.
kubectl apply -k deploy/monitoring/

# Populate the routing-target secrets (Slack webhook URLs, PagerDuty key).
kubectl -n bank-observability create secret generic alertmanager-secrets \
  --from-literal=slack-platform-webhook="$SLACK_PLATFORM" \
  --from-literal=slack-teller-webhook="$SLACK_TELLER" \
  --from-literal=pagerduty-routing-key="$PD_KEY"

# Populate the MinIO bearer token (optional — minio scrape job is otherwise idle).
mc admin prometheus generate myminio cluster | grep '^bearer_token' \
  | awk '{print $2}' \
  | xargs -I{} kubectl -n bank-observability create secret generic prometheus-minio-token \
      --from-literal=token={}

# Populate Grafana admin credentials.
kubectl -n bank-observability create secret generic grafana-admin \
  --from-literal=GF_SECURITY_ADMIN_USER=admin \
  --from-literal=GF_SECURITY_ADMIN_PASSWORD="$GRAFANA_PASS"
```

## How the application is scraped

The Helm chart (`deploy/helm/teller/values.yaml`) renders the following
annotations on every `teller-core` pod:

```yaml
prometheus.io/scrape: "true"
prometheus.io/path: "/actuator/prometheus"
prometheus.io/port: "8080"
prometheus.io/scheme: "http"
```

Prometheus' `kubernetes-pods` scrape job uses these to discover every
backend replica without per-pod configuration. JVM + Spring Boot
metrics are produced by Micrometer's Prometheus registry
(`pom.xml` :: `micrometer-registry-prometheus`).

## Alert routing summary

| Severity   | Receiver                                |
| ---------- | --------------------------------------- |
| `critical` | PagerDuty + Slack (team-specific)       |
| `warning`  | Slack (team-specific)                   |

Teams are tagged on each alert with the `team` label:

* `team=teller-core` → `#teller-core-alerts`
* `team=platform`    → `#bank-platform-alerts`

The Slack channel and PagerDuty integration key are pulled from the
`alertmanager-secrets` Secret at runtime.

## Validating alert rules

```bash
# Requires prometheus's promtool binary.
promtool check rules deploy/monitoring/prometheus/rules/*.yaml
promtool check config deploy/monitoring/prometheus/prometheus.yaml
```

## Validating Grafana dashboards

```bash
# JSON syntax check.
for f in deploy/monitoring/grafana/dashboards/*.json; do
  python3 -m json.tool < "$f" > /dev/null && echo "OK $f"
done
```

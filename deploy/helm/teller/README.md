# teller Helm chart

Helm chart for deploying the Bank-of-Z modernization stack — the Spring Boot
`teller-core` backend, the Next.js `teller-frontend`, the Envoy + OPA sidecars
that front each backend pod, the Kong API Gateway Ingress, the NGINX Ingress
for the SPA, and the cert-manager `ClusterIssuer` + `Certificate` objects that
issue TLS.

## Layout

| Path                                  | Purpose                                     |
| ------------------------------------- | ------------------------------------------- |
| `Chart.yaml`                          | Chart metadata                              |
| `values.yaml`                         | Neutral defaults                            |
| `values-eks.yaml`                     | AWS EKS production overlay (DNS01)          |
| `values-microk8s.yaml`                | VForce360 MicroK8s overlay (HTTP01)         |
| `templates/_helpers.tpl`              | Shared label / name / image helpers         |
| `templates/backend-*.yaml`            | teller-core Deployment + Service + Config + Secret |
| `templates/frontend-*.yaml`           | teller-frontend Deployment + Service        |
| `templates/envoy-configmap.yaml`      | Envoy listener + ext_authz cluster          |
| `templates/opa-configmap.yaml`        | OPA policy bundle + data                    |
| `templates/kong-ingress.yaml`         | Kong-class Ingress for the REST API         |
| `templates/nginx-ingress.yaml`        | NGINX-class Ingress for the SPA             |
| `templates/cert-cluster-issuer.yaml`  | cert-manager ACME ClusterIssuer             |
| `templates/certificates.yaml`         | Per-host Certificate objects                |

## Quick start

```bash
# Validate the chart
helm lint deploy/helm/teller

# Render against the EKS overlay
helm template teller deploy/helm/teller \
  -f deploy/helm/teller/values.yaml \
  -f deploy/helm/teller/values-eks.yaml

# Install on MicroK8s
helm upgrade --install teller deploy/helm/teller \
  -n bank --create-namespace \
  -f deploy/helm/teller/values.yaml \
  -f deploy/helm/teller/values-microk8s.yaml
```

## Sidecar topology

Every backend pod runs three containers:

1. `teller-core` — Spring Boot app, listens on `8080`.
2. `envoy` — front-proxy, listens on `9080`, forwards to `127.0.0.1:8080`.
3. `opa` — policy decision point, gRPC ext_authz on `127.0.0.1:9192`.

The backend `Service` targets the Envoy port `9080`, so every request enters
the pod through Envoy → `envoy.filters.http.ext_authz` → OPA → upstream Spring
Boot. Disable either sidecar in `values.yaml` to fall back to a direct
client-to-app path while debugging.

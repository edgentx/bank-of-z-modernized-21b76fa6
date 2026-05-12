# MinIO metrics — native cluster endpoint

MinIO server ships native Prometheus metrics out of the box; no
separate exporter binary is required.

The metrics path is **`/minio/v2/metrics/cluster`** and it requires a
bearer token. To generate one against the running MinIO operator:

```bash
mc admin prometheus generate myminio cluster
```

The output is a `bearer_token: <jwt>` line. Drop the JWT into a Secret
in the observability namespace so the Prometheus pod can mount it at
`/etc/prometheus/minio/token` (already wired by
`deploy/monitoring/prometheus/deployment.yaml`):

```bash
kubectl -n bank-observability create secret generic prometheus-minio-token \
  --from-literal=token='<JWT>' --dry-run=client -o yaml \
  | kubectl apply -f -
```

Prometheus's `minio` scrape job (in `prometheus.yaml`) targets the
MinIO Service by name (`minio`) on whichever namespace it lives in
(`bank` for in-cluster, `bank-observability` if you co-locate it).
The Service must declare a port named `http` for the relabel filter
to select it.

For a `minio` Service that doesn't already carry the
`prometheus.io/scrape` annotation, the discovery still works because
the `minio` job filters by **service name**, not by annotation.

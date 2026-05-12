#!/bin/sh
# Foreground both Node (Next.js standalone) and nginx in a single container.
# `set -e` plus the SIGTERM trap forwards graceful-shutdown to both processes
# so Kubernetes preStop hooks complete cleanly.
set -eu

node /app/server.js &
NODE_PID=$!

trap 'kill -TERM "$NODE_PID" 2>/dev/null || true; nginx -s quit 2>/dev/null || true; wait' TERM INT

# nginx in foreground (master process) — its exit drives container lifecycle.
exec nginx -g 'daemon off;'

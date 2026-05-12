# syntax=docker/dockerfile:1.7
#
# Bank-of-Z modernization — Next.js teller frontend container.
# Multi-stage:
#   1. `deps`  — installs npm dependencies with a clean lockfile.
#   2. `build` — `next build` emits the production bundle + standalone server.
#   3. `runtime` — nginx + node-on-distroless-style alpine serves traffic.

# ---- Stage 1: deps -----------------------------------------------------------
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
# `npm ci` honors the lockfile exactly and fails closed if it drifts — the
# right choice for reproducible image builds.
RUN npm ci --no-audit --no-fund

# ---- Stage 2: build ----------------------------------------------------------
FROM node:20-alpine AS build
WORKDIR /app
ENV NEXT_TELEMETRY_DISABLED=1
ENV NODE_ENV=production
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# ---- Stage 3: runtime --------------------------------------------------------
# Alpine + nginx for the static-asset + reverse-proxy front, plus node for the
# Next.js standalone server. Both processes run under the same non-root user.
FROM nginx:1.27-alpine AS runtime

# nodejs is required to execute `.next/standalone/server.js`. Tini reaps zombie
# children when the entrypoint backgrounds Node and execs nginx.
RUN apk add --no-cache nodejs tini curl \
    && addgroup -g 10001 -S app \
    && adduser -u 10001 -S app -G app -h /app -s /sbin/nologin \
    && mkdir -p /app /var/cache/nginx /tmp/nginx-client-body /tmp/nginx-proxy \
                /tmp/nginx-fastcgi /tmp/nginx-uwsgi /tmp/nginx-scgi \
    && chown -R app:app /app /var/cache/nginx /var/log/nginx \
                        /tmp/nginx-client-body /tmp/nginx-proxy \
                        /tmp/nginx-fastcgi /tmp/nginx-uwsgi /tmp/nginx-scgi

# Next.js standalone output is structured so that copying these three trees
# into /app produces a runnable bundle at /app/server.js.
WORKDIR /app
COPY --from=build --chown=app:app /app/.next/standalone/ /app/
COPY --from=build --chown=app:app /app/.next/static      /app/.next/static
COPY --from=build --chown=app:app /app/public            /app/public

# Replace the stock nginx config with the reverse-proxy + static-cache config.
COPY docker/nginx.conf       /etc/nginx/nginx.conf
COPY docker/entrypoint.sh    /usr/local/bin/entrypoint.sh
RUN chmod 0755 /usr/local/bin/entrypoint.sh

# Allow nginx to bind port 8080 (well above 1024) as a non-privileged user;
# also ensure the pid path is writable.
RUN touch /tmp/nginx.pid && chown app:app /tmp/nginx.pid /etc/nginx/nginx.conf

ARG VCS_REF=unknown
ARG VERSION=0.0.0
ARG BUILD_DATE=unknown
LABEL org.opencontainers.image.source="https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.title="bank-of-z-teller-frontend" \
      org.opencontainers.image.description="Bank-of-Z modernization — Next.js teller workstation (nginx + standalone)"

USER app:app
WORKDIR /app
EXPOSE 8080

ENV NODE_ENV=production \
    HOSTNAME=127.0.0.1 \
    PORT=3000 \
    NEXT_TELEMETRY_DISABLED=1

HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
    CMD curl --fail --silent http://localhost:8080/healthz || exit 1

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/entrypoint.sh"]

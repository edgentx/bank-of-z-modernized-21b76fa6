# syntax=docker/dockerfile:1.7
#
# Bank-of-Z modernization — Spring Boot backend (teller-core) container.
# Multi-stage:
#   1. `build`  — Maven + JDK 21 compiles the fat jar.
#   2. `runtime` — Eclipse Temurin JRE runs the jar as a non-root user.
#
# The companion `Dockerfile.native` builds a GraalVM AOT-compiled binary for
# the same source tree (used for latency-critical services in S-40).

# ---- Stage 1: build ----------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Maven wrapper would be ideal, but the repo ships the bare pom.xml; install
# Maven from the package index (single-shot apt-get to keep the layer small).
RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

# Resolve dependencies first so the layer is cached when only sources change.
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

# Compile + package. Tests are excluded — the CI workflow runs `mvn verify`
# upstream of the image build, so the container layer doesn't re-run them.
COPY src ./src
COPY features ./features
COPY tests ./tests
RUN mvn -B -q -DskipTests package \
    && cp target/teller-core-*.jar /workspace/app.jar

# ---- Stage 2: runtime --------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

# Non-root user. UID 10001 is well above the host-namespace ranges that map
# into rootful daemons and avoids collision with system accounts shipped in
# the base image.
ARG APP_UID=10001
ARG APP_GID=10001
RUN groupadd --system --gid ${APP_GID} app \
    && useradd --system --uid ${APP_UID} --gid ${APP_GID} \
       --home-dir /app --shell /usr/sbin/nologin app

# curl is needed by the HEALTHCHECK directive; install it before dropping
# privileges so we don't carry the apt cache into the final layer.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /app \
    && chown -R app:app /app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar /app/app.jar

# Container image labels picked up by the build pipeline (S-40 AC: tagged with
# git SHA + semver). The pipeline passes --build-arg VCS_REF / VERSION.
ARG VCS_REF=unknown
ARG VERSION=0.0.0
ARG BUILD_DATE=unknown
LABEL org.opencontainers.image.source="https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.title="bank-of-z-teller-core" \
      org.opencontainers.image.description="Bank-of-Z modernization — Spring Boot teller-core service"

USER app:app
EXPOSE 8000

# Container-aware JVM tuning. -XX:+UseContainerSupport is the default on
# JDK 17+, but ratios beat absolute Xmx when the same image runs across
# t3.medium/m5.large/c6g.xlarge nodes.
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# VForce360's dev deploy path can run this image without the Helm ConfigMap
# that activates vforce_dev. Keep explicit SPRING_PROFILES_ACTIVE values from
# Helm/prod authoritative, but make the standalone container default to the
# authenticated in-cluster Mongo service and embedded DB2-compatible history
# datasource instead of localhost services.
ENV SPRING_PROFILES_DEFAULT="vforce_dev"
ENV SPRING_DATA_MONGODB_URI="mongodb://bank:bank-mongo-dev-pw@bank-mongo:27017/bank?authSource=admin"

# Actuator health probe (S-40 AC: health check endpoints configured).
# 30s start period accommodates JPA validate + Flyway baseline on first boot.
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl --fail --silent http://localhost:8000/actuator/health || exit 1

# Exec form so the JVM is PID 1 and receives SIGTERM for graceful shutdown.
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar \"$@\"", "--"]

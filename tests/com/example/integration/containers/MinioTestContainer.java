package com.example.integration.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * BANK S-44 — singleton MinIO (S3-compatible) Testcontainer for the API
 * integration suite.
 *
 * <p>The {@code minio/minio} image runs the standard MinIO server on port
 * 9000. The {@code MINIO_ROOT_USER} / {@code MINIO_ROOT_PASSWORD} env vars
 * here intentionally mirror the {@code application.properties} defaults
 * ({@code minioadmin}/{@code minioadmin}) — that means the {@link
 * com.example.infrastructure.minio.MinioProperties} bean does not need a
 * per-environment credential override for the integration suite to talk to
 * the container.
 *
 * <p>The container's host port is randomised by Testcontainers; the base
 * class exposes it as the {@code storage.minio.endpoint} property at
 * context-start time so the {@code MinioClient} bean targets the right port.
 */
public final class MinioTestContainer {

  private static final int MINIO_PORT = 9000;
  public static final String ACCESS_KEY = "minioadmin";
  public static final String SECRET_KEY = "minioadmin";

  public static final GenericContainer<?> INSTANCE =
      new GenericContainer<>(DockerImageName.parse("minio/minio:RELEASE.2024-01-16T16-07-38Z"))
          .withExposedPorts(MINIO_PORT)
          .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
          .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
          .withCommand("server", "/data")
          .waitingFor(Wait.forHttp("/minio/health/live").forPort(MINIO_PORT));

  static {
    INSTANCE.start();
  }

  public static String getEndpoint() {
    return "http://" + INSTANCE.getHost() + ":" + INSTANCE.getMappedPort(MINIO_PORT);
  }

  private MinioTestContainer() {
    // singleton holder — no instances
  }
}

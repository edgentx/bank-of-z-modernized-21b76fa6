package com.example.integration.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * BANK S-44 — singleton Redis Testcontainer for the API integration suite.
 *
 * <p>Redis ships no first-party testcontainer module (only Mongo, DB2 etc.
 * have dedicated modules); a {@link GenericContainer} pinned to the
 * {@code redis:7-alpine} image is the canonical idiom and is what the
 * Testcontainers Java docs recommend for any single-port server.
 *
 * <p>The container exposes Redis' default port 6379 inside the container; the
 * host port is randomised by Testcontainers and surfaced via
 * {@link #getMappedPort(int)}, which the base class wires into the
 * {@code cache.redis.port} property at context-start time.
 */
public final class RedisTestContainer {

  private static final int REDIS_PORT = 6379;

  public static final GenericContainer<?> INSTANCE =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
          .withExposedPorts(REDIS_PORT)
          .waitingFor(Wait.forListeningPort());

  static {
    INSTANCE.start();
  }

  public static String getHost() {
    return INSTANCE.getHost();
  }

  public static int getPort() {
    return INSTANCE.getMappedPort(REDIS_PORT);
  }

  private RedisTestContainer() {
    // singleton holder — no instances
  }
}

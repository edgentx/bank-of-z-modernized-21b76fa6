package com.example.integration.containers;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * BANK S-44 — singleton MongoDB Testcontainer for the API integration suite.
 *
 * <p>One container is booted per JVM via the static {@code INSTANCE} field and
 * shared across every {@code *IntegrationTest} class. Reusing the container is
 * critical to honour the AC "Test execution time is under 10 minutes for full
 * suite": a per-test-class start would add ~10 s × N test classes.
 *
 * <p>Spring is wired to the container via
 * {@code @DynamicPropertySource} in {@link BaseApiIntegrationTest}.
 *
 * <p>The Mongo {@code 6.0} image is used because the production deployment
 * targets Mongo 6.x (Spring Data Mongo 4.x baseline) — running tests against
 * an older 4.4 image would mask wire-protocol regressions that the prod cluster
 * would surface.
 */
public final class MongoTestContainer {

  /**
   * Shared singleton — started lazily and reused across all tests in the JVM.
   * Testcontainers' Ryuk reaper tears it down when the surefire/failsafe JVM
   * exits, so no explicit @AfterAll is required.
   */
  public static final MongoDBContainer INSTANCE =
      new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
          .withReuse(false);

  static {
    INSTANCE.start();
  }

  private MongoTestContainer() {
    // singleton holder — no instances
  }
}

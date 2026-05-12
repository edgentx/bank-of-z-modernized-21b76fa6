package com.example.integration;

import com.example.integration.containers.Db2TestContainer;
import com.example.integration.containers.MinioTestContainer;
import com.example.integration.containers.MongoTestContainer;
import com.example.integration.containers.RedisTestContainer;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * BANK S-44 — base class for the API-level integration suite.
 *
 * <p>Wires a real Spring Boot context on a random HTTP port and points the
 * four backing-store adapters (Mongo / DB2-or-H2 / Redis / MinIO) at the
 * shared Testcontainers singletons. Subclasses extend this class and use
 * {@code TestRestTemplate} to exercise the real REST controllers end-to-end.
 *
 * <p>The {@link DynamicPropertySource} hook runs after the static container
 * fields are initialised but before Spring builds the bean context, so every
 * adapter bean wakes up pointed at the container's randomised host port.
 *
 * <p>Docker availability is a hard prerequisite for this suite. The class is
 * gated by {@code @EnabledIfEnvironmentVariable} on the
 * {@code BANK_INTEGRATION_TESTS} env var (set by CI) — locally, the suite is
 * skipped unless the developer explicitly opts in by setting the variable,
 * preserving fast feedback for unit-only runs.
 *
 * <p>DB2 has two modes:
 * <ul>
 *   <li><b>Default</b>: H2 in DB2-compatibility mode (matches the existing
 *       S-29 pattern, ~ms startup).</li>
 *   <li><b>{@code -P db2-integration}</b>: real DB2 container via
 *       {@link Db2TestContainer} — adds ~5 min of startup, only enabled on
 *       hosts with disk + time budget.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "BANK_INTEGRATION_TESTS", matches = "true")
public abstract class BaseApiIntegrationTest {

  @LocalServerPort
  protected int port;

  @Autowired(required = false)
  protected org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

  @DynamicPropertySource
  static void registerContainerProperties(DynamicPropertyRegistry registry) {
    // MongoDB — Spring Data Mongo reads spring.data.mongodb.uri.
    registry.add("spring.data.mongodb.uri",
        () -> MongoTestContainer.INSTANCE.getReplicaSetUrl("bank"));

    // Redis — RedisCacheConfig reads cache.redis.host / cache.redis.port.
    registry.add("cache.redis.host", RedisTestContainer::getHost);
    registry.add("cache.redis.port", () -> Integer.toString(RedisTestContainer.getPort()));
    registry.add("cache.redis.password", () -> "");

    // MinIO — MinioClientConfig reads storage.minio.endpoint / keys.
    registry.add("storage.minio.endpoint", MinioTestContainer::getEndpoint);
    registry.add("storage.minio.access-key", () -> MinioTestContainer.ACCESS_KEY);
    registry.add("storage.minio.secret-key", () -> MinioTestContainer.SECRET_KEY);

    // DB2 / H2 — the JDBC URL switches based on the db2-integration profile.
    if (Db2TestContainer.isEnabled()) {
      var db2 = Db2TestContainer.get();
      registry.add("spring.datasource.url", db2::getJdbcUrl);
      registry.add("spring.datasource.username", db2::getUsername);
      registry.add("spring.datasource.password", db2::getPassword);
      registry.add("spring.datasource.driver-class-name",
          () -> "com.ibm.db2.jcc.DB2Driver");
      registry.add("spring.jpa.database-platform",
          () -> "org.hibernate.dialect.DB2Dialect");
    } else {
      registry.add("spring.datasource.url",
          () -> "jdbc:h2:mem:bank-it;MODE=DB2;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
      registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
      registry.add("spring.datasource.username", () -> "sa");
      registry.add("spring.datasource.password", () -> "");
      registry.add("spring.jpa.database-platform",
          () -> "org.hibernate.dialect.H2Dialect");
    }

    // The JMS and Temporal stacks are gated by their own enabled flags in
    // application.properties; we leave them disabled for the integration
    // suite — those subsystems get their own dedicated tests
    // (Ibm*JmsSendReceiveIntegrationTest, *WorkflowTest).
    registry.add("messaging.ibmmq.enabled", () -> "false");
    registry.add("messaging.ibmmq.listener-enabled", () -> "false");
    registry.add("workflow.temporal.worker-enabled", () -> "false");
    registry.add("telemetry.otel.enabled", () -> "false");
    registry.add("management.otlp.metrics.export.enabled", () -> "false");
  }

  /**
   * Convenience: full {@code http://localhost:<port>/api/...} URL prefix
   * for the controller under test. Subclasses concatenate the controller
   * path to this in their TestRestTemplate calls.
   */
  protected String url(String path) {
    return "http://localhost:" + port + path;
  }
}

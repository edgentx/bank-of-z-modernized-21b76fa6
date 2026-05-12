package com.example.integration.containers;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;

/**
 * BANK S-44 — opt-in IBM DB2 Testcontainer.
 *
 * <p>The official {@code icr.io/db2_community/db2} image is ~5 GiB and
 * ~5 minutes to start, which would breach the "Test execution time is under
 * 10 minutes" AC if every CI run pulled it. The default integration path
 * therefore uses H2 in DB2-compatibility mode (the existing S-29 pattern,
 * already used by {@code HistoryRepositoriesIntegrationTest}). This class
 * exists so the Testcontainers configuration is preserved for hosts that
 * can afford the full DB2 fidelity run — activate it via the
 * {@code db2-integration} Maven profile (mvn -P db2-integration verify) or
 * directly via {@code -Dbank.testcontainers.db2.enabled=true}.
 *
 * <p>The {@link #isEnabled()} helper lets test classes decide at runtime
 * whether to wire the JDBC URL to this container vs. the H2 fallback.
 */
public final class Db2TestContainer {

  public static final String ENABLED_PROP = "bank.testcontainers.db2.enabled";

  private static volatile Db2Container instance;

  /**
   * True when the {@code bank.testcontainers.db2.enabled} system property is
   * set to {@code true}. The base class checks this before instantiating
   * the container so a default CI run never pays the DB2 startup cost.
   */
  public static boolean isEnabled() {
    return Boolean.parseBoolean(System.getProperty(ENABLED_PROP, "false"));
  }

  /**
   * Lazily-started DB2 container. Only call when {@link #isEnabled()} is
   * true; the first caller boots the container, every subsequent caller
   * reuses the same instance for the lifetime of the JVM.
   */
  public static Db2Container get() {
    Db2Container c = instance;
    if (c == null) {
      synchronized (Db2TestContainer.class) {
        c = instance;
        if (c == null) {
          c = new Db2Container(DockerImageName.parse("icr.io/db2_community/db2:11.5.9.0"))
              .acceptLicense();
          c.start();
          instance = c;
        }
      }
    }
    return c;
  }

  private Db2TestContainer() {
    // singleton holder — no instances
  }
}

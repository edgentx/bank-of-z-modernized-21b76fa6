package com.example.infrastructure.temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * BANK S-33 — coverage for {@link TemporalProperties} default values and
 * retry-policy nested binding.
 *
 * <p>The acceptance criterion "Workflow timeouts and retry policies are
 * configurable" means the {@code workflow.temporal.*} prefix has to round-
 * trip cleanly through {@code @ConfigurationProperties}; these tests pin
 * the defaults so an accidental rename of a setter breaks the build rather
 * than silently dropping a tunable.
 */
class TemporalPropertiesTest {

  @Test
  void defaultsAreSafeForLocalDev() {
    TemporalProperties props = new TemporalProperties();

    assertEquals("127.0.0.1:7233", props.getTarget());
    assertEquals("bank", props.getNamespace());
    assertEquals("bank-tasks", props.getTaskQueue());
    assertFalse(props.isWorkerEnabled(), "worker disabled by default — opt-in via env var");
    assertEquals(Duration.ofMinutes(10), props.getWorkflowExecutionTimeout());
    assertEquals(Duration.ofSeconds(30), props.getActivityStartToCloseTimeout());
    assertEquals(Duration.ofMinutes(2), props.getActivityScheduleToCloseTimeout());
  }

  @Test
  void retryPolicyDefaultsExponentialBackoff() {
    TemporalProperties.Retry retry = new TemporalProperties().getRetry();

    assertEquals(Duration.ofSeconds(1), retry.getInitialInterval());
    assertEquals(Duration.ofSeconds(30), retry.getMaximumInterval());
    assertEquals(2.0, retry.getBackoffCoefficient(), 1e-9);
    assertEquals(5, retry.getMaximumAttempts());
  }

  @Test
  void settersUpdateValuesAndWorkerCanBeEnabled() {
    TemporalProperties props = new TemporalProperties();
    props.setTarget("temporal.svc.cluster.local:7233");
    props.setNamespace("bank-prod");
    props.setTaskQueue("bank-prod-tasks");
    props.setWorkerEnabled(true);
    props.setWorkflowExecutionTimeout(Duration.ofHours(1));

    assertEquals("temporal.svc.cluster.local:7233", props.getTarget());
    assertEquals("bank-prod", props.getNamespace());
    assertEquals("bank-prod-tasks", props.getTaskQueue());
    assertTrue(props.isWorkerEnabled());
    assertEquals(Duration.ofHours(1), props.getWorkflowExecutionTimeout());
  }
}

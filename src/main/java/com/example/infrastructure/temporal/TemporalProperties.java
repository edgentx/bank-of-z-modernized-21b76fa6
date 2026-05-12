package com.example.infrastructure.temporal;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BANK S-33 — externalized config for the Temporal workflow adapter.
 *
 * <p>Bound to {@code workflow.temporal.*} in {@code application.properties}
 * (and overridable per environment via {@code WORKFLOW_TEMPORAL_*} env vars).
 * A dedicated {@code workflow.temporal.*} prefix (rather than reusing
 * Temporal's own conventional {@code spring.temporal.*}) keeps the SDK's
 * alpha spring-boot-starter out of the dependency graph — we wire beans by
 * hand in {@link TemporalConfig} the same way the Redis/Minio adapters do.
 *
 * <p>The {@link Retry} inner type holds the activity retry policy fields
 * (initial interval, backoff coefficient, max interval, max attempts) so
 * those four knobs can be tuned without redeploying — the {@link #buildRetryOptions()}
 * helper materializes them into a Temporal {@code RetryOptions} instance for
 * the workflow stubs to use.
 *
 * <p>{@link #isWorkerEnabled()} defaults to {@code false} so unit tests and
 * the embedded H2 BDD suite never need a running Temporal server. Container
 * deployments flip it on via {@code WORKFLOW_TEMPORAL_WORKER_ENABLED=true}.
 */
@ConfigurationProperties(prefix = "workflow.temporal")
public class TemporalProperties {

  /** gRPC target (host:port) of the Temporal frontend service. */
  private String target = "127.0.0.1:7233";

  /** Temporal namespace the worker registers under. */
  private String namespace = "bank";

  /** Task queue the worker polls. Workflows are routed by this string. */
  private String taskQueue = "bank-tasks";

  /**
   * Whether to start the embedded worker on application boot. Off by default
   * so plain unit tests do not need a Temporal server.
   */
  private boolean workerEnabled = false;

  /** Top-level workflow execution timeout (StartToClose). */
  private Duration workflowExecutionTimeout = Duration.ofMinutes(10);

  /**
   * Default activity start-to-close timeout. This is the per-attempt limit
   * the activity has to complete each time it is dispatched to a worker.
   */
  private Duration activityStartToCloseTimeout = Duration.ofSeconds(30);

  /**
   * Default activity schedule-to-close timeout — the wall-clock cap that
   * spans all retry attempts. Once this elapses Temporal stops retrying
   * regardless of the retry policy.
   */
  private Duration activityScheduleToCloseTimeout = Duration.ofMinutes(2);

  /** Activity retry policy. */
  private Retry retry = new Retry();

  public String getTarget() { return target; }
  public void setTarget(String target) { this.target = target; }

  public String getNamespace() { return namespace; }
  public void setNamespace(String namespace) { this.namespace = namespace; }

  public String getTaskQueue() { return taskQueue; }
  public void setTaskQueue(String taskQueue) { this.taskQueue = taskQueue; }

  public boolean isWorkerEnabled() { return workerEnabled; }
  public void setWorkerEnabled(boolean workerEnabled) { this.workerEnabled = workerEnabled; }

  public Duration getWorkflowExecutionTimeout() { return workflowExecutionTimeout; }
  public void setWorkflowExecutionTimeout(Duration workflowExecutionTimeout) {
    this.workflowExecutionTimeout = workflowExecutionTimeout;
  }

  public Duration getActivityStartToCloseTimeout() { return activityStartToCloseTimeout; }
  public void setActivityStartToCloseTimeout(Duration activityStartToCloseTimeout) {
    this.activityStartToCloseTimeout = activityStartToCloseTimeout;
  }

  public Duration getActivityScheduleToCloseTimeout() { return activityScheduleToCloseTimeout; }
  public void setActivityScheduleToCloseTimeout(Duration activityScheduleToCloseTimeout) {
    this.activityScheduleToCloseTimeout = activityScheduleToCloseTimeout;
  }

  public Retry getRetry() { return retry; }
  public void setRetry(Retry retry) { this.retry = retry; }

  /** Activity retry policy — initial interval, backoff, max interval, max attempts. */
  public static class Retry {
    private Duration initialInterval = Duration.ofSeconds(1);
    private Duration maximumInterval = Duration.ofSeconds(30);
    private double backoffCoefficient = 2.0;
    private int maximumAttempts = 5;

    public Duration getInitialInterval() { return initialInterval; }
    public void setInitialInterval(Duration initialInterval) { this.initialInterval = initialInterval; }

    public Duration getMaximumInterval() { return maximumInterval; }
    public void setMaximumInterval(Duration maximumInterval) { this.maximumInterval = maximumInterval; }

    public double getBackoffCoefficient() { return backoffCoefficient; }
    public void setBackoffCoefficient(double backoffCoefficient) { this.backoffCoefficient = backoffCoefficient; }

    public int getMaximumAttempts() { return maximumAttempts; }
    public void setMaximumAttempts(int maximumAttempts) { this.maximumAttempts = maximumAttempts; }
  }
}

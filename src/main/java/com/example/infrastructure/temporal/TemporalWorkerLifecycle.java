package com.example.infrastructure.temporal;

import com.example.infrastructure.temporal.activity.AccountOpeningActivities;
import com.example.infrastructure.temporal.activity.LegacyBridgeActivities;
import com.example.infrastructure.temporal.workflow.AccountOpeningWorkflowImpl;
import com.example.infrastructure.temporal.workflow.LegacyTransactionRouteWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * BANK S-33 — Spring-managed lifecycle for the Temporal worker.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>On {@code @PostConstruct}: register the workflow types and activity
 *       implementations on a worker bound to the configured task queue,
 *       then start the {@link WorkerFactory}.</li>
 *   <li>On {@code @PreDestroy}: drain in-flight tasks and shut the worker
 *       factory down cleanly so Kubernetes pod terminations do not strand
 *       half-completed workflow histories.</li>
 * </ul>
 *
 * <p>The bean is a no-op when {@code workflow.temporal.worker-enabled=false}
 * (the default) so unit tests, the H2 BDD suite, and dev environments that
 * do not have Temporal running can boot the Spring context without a worker
 * trying to dial an unreachable gRPC target. Production containers flip the
 * flag on via {@code WORKFLOW_TEMPORAL_WORKER_ENABLED=true}.
 *
 * <p>Activity beans are injected by Spring; the workflow <em>implementation
 * classes</em> are registered by type because Temporal instantiates them per
 * execution (workflow instances are not Spring beans — they must be
 * deterministically constructible from the workflow id alone).
 */
@Component
public class TemporalWorkerLifecycle {

  private static final Logger LOG = LoggerFactory.getLogger(TemporalWorkerLifecycle.class);

  private final WorkerFactory workerFactory;
  private final TemporalProperties props;
  private final AccountOpeningActivities accountOpeningActivities;
  private final LegacyBridgeActivities legacyBridgeActivities;

  /**
   * The {@code @Autowired} on the constructor is explicit so test contexts
   * that lack one of the activity beans (e.g. a slice test focused on the
   * config wiring) can substitute a mock without rewriting the signature.
   */
  @Autowired
  public TemporalWorkerLifecycle(WorkerFactory workerFactory,
                                 TemporalProperties props,
                                 AccountOpeningActivities accountOpeningActivities,
                                 LegacyBridgeActivities legacyBridgeActivities) {
    this.workerFactory = workerFactory;
    this.props = props;
    this.accountOpeningActivities = accountOpeningActivities;
    this.legacyBridgeActivities = legacyBridgeActivities;
  }

  @PostConstruct
  public void start() {
    if (!props.isWorkerEnabled()) {
      LOG.info("Temporal worker disabled (workflow.temporal.worker-enabled=false); skipping registration");
      return;
    }
    Worker worker = workerFactory.newWorker(props.getTaskQueue());
    worker.registerWorkflowImplementationTypes(
        AccountOpeningWorkflowImpl.class,
        LegacyTransactionRouteWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountOpeningActivities, legacyBridgeActivities);
    workerFactory.start();
    LOG.info("Temporal worker started: namespace='{}' taskQueue='{}' target='{}'",
        props.getNamespace(), props.getTaskQueue(), props.getTarget());
  }

  @PreDestroy
  public void stop() {
    if (!props.isWorkerEnabled()) {
      return;
    }
    LOG.info("Shutting down Temporal worker factory");
    workerFactory.shutdown();
  }
}

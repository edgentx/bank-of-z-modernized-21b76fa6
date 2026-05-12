package com.example.infrastructure.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BANK S-33 — Spring wiring for the Temporal workflow adapter.
 *
 * <p>Keeping the {@link WorkflowServiceStubs}, {@link WorkflowClient}, and
 * {@link WorkerFactory} construction in a dedicated {@code @Configuration}
 * (rather than {@code @Component} on the adapter) means tests can swap any
 * of them for a {@code TestWorkflowEnvironment}-issued bean without dragging
 * in the full property binder or opening a real gRPC connection. This mirrors
 * the {@link com.example.infrastructure.redis.RedisCacheConfig} pattern from
 * S-32 and {@link com.example.infrastructure.minio.MinioClientConfig} from
 * S-31.
 *
 * <p>The {@link WorkerFactory} bean is constructed unconditionally so the
 * {@link TemporalWorkerLifecycle} can decide at runtime whether to actually
 * register workflows and start the worker (driven by
 * {@code workflow.temporal.worker-enabled}). Tests construct their own
 * factory off the in-process test service and never go through this bean.
 */
@Configuration
@EnableConfigurationProperties(TemporalProperties.class)
public class TemporalConfig {

  /**
   * Build the gRPC service stub against the configured Temporal frontend.
   * The stub holds the long-lived gRPC channel; it is thread-safe and shared
   * across the client and worker factory.
   */
  @Bean
  public WorkflowServiceStubs workflowServiceStubs(TemporalProperties props) {
    return WorkflowServiceStubs.newServiceStubs(
        WorkflowServiceStubsOptions.newBuilder()
            .setTarget(props.getTarget())
            .build());
  }

  /**
   * Build the high-level {@link WorkflowClient} used by the orchestration
   * adapter to start, signal, query, and cancel workflows. The namespace
   * lives here (rather than on the stubs) because a single Temporal cluster
   * typically serves multiple namespaces.
   */
  @Bean
  public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs, TemporalProperties props) {
    return WorkflowClient.newInstance(
        serviceStubs,
        WorkflowClientOptions.newBuilder()
            .setNamespace(props.getNamespace())
            .build());
  }

  /**
   * Build the worker factory the application uses to host workflow + activity
   * implementations. The factory is paused until
   * {@link TemporalWorkerLifecycle} calls {@code start()} — registering
   * workflow types before {@code start()} is the SDK's required ordering.
   */
  @Bean
  public WorkerFactory workerFactory(WorkflowClient client) {
    return WorkerFactory.newInstance(client);
  }
}

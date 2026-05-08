package com.example.infra.config;

import com.example.application.DefectReportingActivity;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.workflow.ReportDefectWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal Worker.
 * Registers workflows and activities with the Temporal task queue.
 */
@Configuration
public class TemporalWorkerConfig {

    private static final Logger log = LoggerFactory.getLogger(TemporalWorkerConfig.class);
    private static final String TASK_QUEUE = "DEFECT_TASK_QUEUE";

    public TemporalWorkerConfig(
        WorkflowClient workflowClient,
        GitHubIssuePort gitHubPort,
        SlackNotificationPort slackPort
    ) {
        // Create a worker factory
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        // Create a worker for the task queue
        Worker worker = factory.newWorker(TASK_QUEUE);

        // Register Workflow implementation
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);

        // Register Activity implementation with real adapters
        // Note: We wrap the interface impl in the class structure expected by the test/worker
        worker.registerActivitiesImplementations(new DefectReportingActivity.Impl(gitHubPort, slackPort));

        // Start the worker
        factory.start();
        log.info("Temporal Worker started successfully for queue: {}", TASK_QUEUE);
    }

    @Bean
    public WorkflowClient workflowClient(io.temporal.serviceclient.WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs);
    }
}

package com.example.config;

import com.example.domain.reconciliation.ReportDefectActivitiesImpl;
import com.example.domain.reconciliation.ReportDefectWorkflowImpl;
import com.example.ports.SlackNotificationPort;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class TemporalWorkerConfig {

    private final WorkflowClient workflowClient;
    private final ReportDefectActivitiesImpl activities;
    private final SlackNotificationPort slackNotificationPort;

    private WorkerFactory workerFactory;

    public TemporalWorkerConfig(WorkflowClient workflowClient,
                                ReportDefectActivitiesImpl activities,
                                SlackNotificationPort slackNotificationPort) {
        this.workflowClient = workflowClient;
        this.activities = activities;
        this.slackNotificationPort = slackNotificationPort;
    }

    @PostConstruct
    public void startWorker() {
        workerFactory = WorkerFactory.newInstance(workflowClient);

        // Define task queue
        String taskQueue = "DEFECT_TASK_QUEUE";

        // Create worker
        Worker worker = workerFactory.newWorker(taskQueue);

        // Register Workflow
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);

        // Register Activities with shared dependencies (Spring Bean)
        worker.registerActivitiesImplementations(activities);

        // Start worker
        workerFactory.start();
    }

    @PreDestroy
    public void shutdownWorker() {
        if (workerFactory != null) {
            workerFactory.shutdown();
        }
    }
}
package com.example.config;

import com.example.ports.SlackNotificationPort;
import com.example.workflow.DefectActivities;
import com.example.workflow.DefectActivitiesImpl;
import com.example.workflow.ReportDefectWorkflow;
import com.example.workflow.ReportDefectWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Configuration class to register Temporal Workflows and Activities.
 * This wires up the real implementation with the Slack adapter upon application startup.
 */
@Component
public class TemporalWorkerConfig {

    private final WorkflowClient workflowClient;
    private final SlackNotificationPort slackNotificationPort;

    public TemporalWorkerConfig(WorkflowClient workflowClient, SlackNotificationPort slackNotificationPort) {
        this.workflowClient = workflowClient;
        this.slackNotificationPort = slackNotificationPort;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorker() {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        
        // Define the task queue
        String taskQueue = "DEFECT_TASK_QUEUE";
        Worker worker = factory.newWorker(taskQueue);

        // Register Workflow
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);

        // Register Activities with dependencies
        // We register the implementation class, and pass the required dependencies (Slack Port)
        DefectActivities activities = new DefectActivitiesImpl(slackNotificationPort);
        worker.registerActivitiesImplementations(activities);

        // Start the worker
        factory.start();
        System.out.println("Temporal Worker started for Task Queue: " + taskQueue);
    }
}

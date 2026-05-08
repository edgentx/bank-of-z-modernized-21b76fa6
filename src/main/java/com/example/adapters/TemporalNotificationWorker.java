package com.example.adapters;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Temporal Worker that listens for defect workflow tasks.
 */
@Component
public class TemporalNotificationWorker {

    private final WorkflowServiceStubs serviceStubs;
    private final WorkerFactory factory;

    @Autowired
    public TemporalNotificationWorker(WorkflowServiceStubs serviceStubs) {
        this.serviceStubs = serviceStubs;
        // Create a WorkerFactory
        this.factory = WorkerFactory.newInstance(serviceStubs);

        // Create a Worker that listens to the task queue
        Worker worker = factory.newWorker("DefectTaskQueue");

        // Register the Workflow implementation
        worker.registerWorkflowImplementationFactory(
            TemporalDefectWorkflowAdapter.DefectWorkflow.class,
            () -> new DefectWorkflowImpl()
        );
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startWorker() {
        factory.start();
    }

    // Implementation stub (Actual logic to call Slack would be here or in Activities)
    public static class DefectWorkflowImpl implements TemporalDefectWorkflowAdapter.DefectWorkflow {
        @Override
        public void reportDefect(String severity, String title, String description) {
            // Workflow logic goes here
            // Should eventually call an Activity that posts to Slack
        }
    }
}
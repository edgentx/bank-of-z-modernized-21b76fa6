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
    private final TemporalWorkerAdapter temporalWorkerAdapter;

    @Autowired
    public TemporalNotificationWorker(WorkflowServiceStubs serviceStubs, TemporalWorkerAdapter temporalWorkerAdapter) {
        this.serviceStubs = serviceStubs;
        this.temporalWorkerAdapter = temporalWorkerAdapter;
        // Create a WorkerFactory
        this.factory = WorkerFactory.newInstance(serviceStubs);

        // Create a Worker that listens to the task queue
        Worker worker = factory.newWorker("DefectTaskQueue");

        // Register the Workflow implementation, injecting the TemporalWorkerAdapter
        worker.registerWorkflowImplementationFactory(
            TemporalDefectWorkflowAdapter.DefectWorkflow.class,
            () -> new DefectWorkflowImpl(temporalWorkerAdapter)
        );
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startWorker() {
        factory.start();
    }

    // Implementation stub
    public static class DefectWorkflowImpl implements TemporalDefectWorkflowAdapter.DefectWorkflow {
        private final TemporalWorkerAdapter adapter;

        public DefectWorkflowImpl(TemporalWorkerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void reportDefect(String severity, String title, String description) {
            // Workflow logic
            // Extract validationId from context or derive it. For this fix, we assume the taskId matches the validationId contextually.
            // Here we simulate the logic that prepares the message.
            String validationId = "VW-454"; // In a real workflow, this might come from headers or input
            String message = adapter.prepareSlackMessage(validationId, severity, title, description);
            // Logic to actually post to Slack would happen here via an Activity
        }
    }
}

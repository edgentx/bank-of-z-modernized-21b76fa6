package com.example.adapters;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter to trigger Temporal workflows for defect reporting.
 */
@Component
public class TemporalDefectWorkflowAdapter {

    private final WorkflowClient client;

    @Autowired
    public TemporalDefectWorkflowAdapter(WorkflowClient client) {
        this.client = client;
    }

    public void reportDefect(String taskId, String severity, String title, String description) {
        // Create a new workflow stub
        // Workflow ID is deterministic based on taskId for idempotency
        DefectWorkflow workflow = client.newWorkflowStub(
            DefectWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("DefectTaskQueue")
                .setWorkflowId(taskId)
                .build()
        );

        // Execute workflow
        workflow.reportDefect(severity, title, description);
    }

    @WorkflowInterface
    public interface DefectWorkflow {
        @WorkflowMethod
        void reportDefect(String severity, String title, String description);
    }
}
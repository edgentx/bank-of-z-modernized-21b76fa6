package com.example.adapters;

import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.ports.DefectWorkflowPort;
import com.example.workflow.DefectReportingWorkflow;
import io.temporal.client.WorkflowClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Adapter implementation for DefectWorkflowPort using Temporal.
 * Bridges the domain/application layer to the Temporal workflow engine.
 */
@Service
public class TemporalDefectWorkflowAdapter implements DefectWorkflowPort {

    private static final Logger log = LoggerFactory.getLogger(TemporalDefectWorkflowAdapter.class);
    private final WorkflowClient workflowClient;

    public TemporalDefectWorkflowAdapter(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @Override
    public void reportDefect(ReportDefectCommand command) {
        log.info("Adapter: Triggering defect report for {}", command.defectId());

        // Create a workflow stub
        DefectReportingWorkflow workflow = workflowClient.newWorkflowStub(
            DefectReportingWorkflow.class,
            "Defect-" + command.defectId()
        );

        // Execute workflow (synchronously for this demo, or async via WorkflowClient.start)
        // Using synchronous execution for immediate feedback in this context.
        String result = workflow.reportDefect(command);
        log.info("Workflow result: {}", result);
    }
}

package com.example.domain.validation.model;

import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporal Workflow implementation for reporting defects.
 * Interacts with the domain layer to process the defect report.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    String reportDefect(String validationId, String defectId, String title);
}

/**
 * Workflow Implementation.
 */
class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflowImpl.class);

    @Override
    public String reportDefect(String validationId, String defectId, String title) {
        log.info("Starting defect report workflow for: {} - {}", defectId, title);
        
        // Simulate workflow logic (e.g., activity calls to external services like GitHub/Slack)
        // For S-FB-1, we ensure the URL generation logic is sound.
        
        Workflow.sleep(1000); // Simulate processing
        
        String generatedUrl = "https://github.com/egdcrypto-bank-of-z/issues/" + defectId;
        log.info("Defect reported successfully. GitHub URL: {}", generatedUrl);
        
        return generatedUrl;
    }
}

package com.example.domain.validation.model;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for reporting defects.
 * This interface defines the contract that the Temporal Worker executes.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Submits a defect report to the VForce360 system.
     * Expected to result in a Slack notification containing a GitHub URL.
     *
     * @param defectTitle The title of the defect.
     * @param description A detailed description of the defect.
     * @return The URL of the created GitHub issue.
     */
    @WorkflowMethod
    String reportDefect(String defectTitle, String description);
}

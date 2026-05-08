package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * This orchestrates creating the GitHub issue and notifying Slack.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Workflow entry point.
     *
     * @param defectDescription Description of the defect.
     * @return The GitHub Issue URL.
     */
    @WorkflowMethod
    String reportDefect(String defectDescription);
}

package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * Orchestrates the creation of a GitHub issue and subsequent notification via Slack.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Executes the defect reporting saga.
     * 1. Create GitHub Issue.
     * 2. Post to Slack with the GitHub URL included.
     *
     * @param defectTitle Title of the defect.
     * @param defectBody Description of the defect.
     * @return The GitHub Issue URL.
     */
    @WorkflowMethod
    String reportDefect(String defectTitle, String defectBody);
}
package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * Orchestrates the interaction between domain logic and external notifications.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Initiates the defect reporting process.
     *
     * @param defectId The unique ID of the defect.
     * @param githubUrl The URL of the GitHub issue.
     * @param slackChannel The target Slack channel.
     * @return A confirmation string.
     */
    @WorkflowMethod
    String reportDefect(String defectId, String githubUrl, String slackChannel);
}

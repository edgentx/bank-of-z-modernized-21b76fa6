package com.example.workflow;

import com.example.domain.validation.model.SlackMessageBody;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface for reporting a defect.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Executes the defect reporting saga.
     * 1. Create GitHub Issue
     * 2. Compose Slack Body with GitHub URL
     * 3. Notify Slack
     *
     * @param title       Title of the defect
     * @param description Description of the defect
     * @return The final Slack message body sent (for verification)
     */
    @WorkflowMethod
    SlackMessageBody reportDefect(String title, String description);
}

package com.example.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting a defect.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Executes the defect reporting workflow.
     *
     * @param title The title of the defect.
     * @return The URL of the created GitHub issue.
     */
    @WorkflowMethod
    String report(String title);
}
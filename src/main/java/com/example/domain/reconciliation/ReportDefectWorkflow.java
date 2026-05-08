package com.example.domain.reconciliation;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting a defect.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Reports a defect with a specific GitHub URL.
     * This workflow orchestrates the formatting and sending of the notification.
     *
     * @param githubUrl The URL of the GitHub issue to report.
     */
    @WorkflowMethod
    void reportDefect(String githubUrl);
}
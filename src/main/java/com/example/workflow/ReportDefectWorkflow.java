package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow orchestration for reporting a defect.
 * Ensures that GitHub URL is included in Slack notifications.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    String reportDefect(String defectId, String description);
}
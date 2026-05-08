package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface for reporting a defect.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String execute(String summary, String description, String slackChannel);
}

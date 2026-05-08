package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * Story: S-FB-1
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(String description, String severity);
}

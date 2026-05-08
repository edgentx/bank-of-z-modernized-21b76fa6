package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface for reporting defects.
 * Changed to public to fix visibility compilation error.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(String title, String description);
}

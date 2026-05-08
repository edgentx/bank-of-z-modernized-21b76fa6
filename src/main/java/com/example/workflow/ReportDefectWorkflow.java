package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow Interface definition.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    void reportDefect(String defectId, String description);
}
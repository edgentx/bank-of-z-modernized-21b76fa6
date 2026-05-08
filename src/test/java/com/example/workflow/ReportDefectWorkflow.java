package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Interface for the defect reporting workflow.
 * This is the contract the test verifies.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {
    @WorkflowMethod
    void reportDefect(String issueId, String description);
}

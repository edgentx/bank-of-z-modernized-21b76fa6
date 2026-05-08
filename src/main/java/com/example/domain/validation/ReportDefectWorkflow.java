package com.example.domain.validation;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for reporting a defect.
 * Expected to be triggered via temporal-worker exec.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void reportDefect(String defectId, String title, String description);
}

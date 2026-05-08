package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for reporting defects.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(String defectTitle, String defectBody);
}

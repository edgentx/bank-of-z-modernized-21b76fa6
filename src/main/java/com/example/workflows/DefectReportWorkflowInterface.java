package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface.
 */
@WorkflowInterface
public interface DefectReportWorkflowInterface {
    @WorkflowMethod
    String reportDefect(String title, String body);
}
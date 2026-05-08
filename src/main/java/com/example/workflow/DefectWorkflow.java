package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition.
 */
@WorkflowInterface
public interface DefectWorkflow {

    @WorkflowMethod
    String executeReportDefect(String projectId, String title, String description);
}
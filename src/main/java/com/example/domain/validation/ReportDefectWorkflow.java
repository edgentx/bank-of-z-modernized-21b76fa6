package com.example.domain.validation;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition.
 * Workflows define the orchestration logic. In this simple case,
 * it's a direct call to the activity.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void execute(String title, String body);
}

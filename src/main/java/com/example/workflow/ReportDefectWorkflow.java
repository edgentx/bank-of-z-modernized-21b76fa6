package com.example.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * Orchestrates the validation process and Slack notification.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    /**
     * Executes the defect reporting process.
     *
     * @param command The command containing defect details.
     */
    @WorkflowMethod
    void execute(ReportDefectCommand command);
}

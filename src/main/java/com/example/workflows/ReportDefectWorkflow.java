package com.example.workflows;

import com.example.domain.validation.ReportDefectCmd;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow definition for reporting a defect.
 * Orchestrates GitHub Issue creation and Slack notification.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void report(ReportDefectCmd cmd);
}

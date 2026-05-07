package com.example.adapters;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for reporting defects.
 * Orchestrates the call to VForce360 and subsequent Slack notification.
 */
@WorkflowInterface
public interface TemporalReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(String defectDetails);
}

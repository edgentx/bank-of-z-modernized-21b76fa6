package com.example.domain.validation.model;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow Interface for reporting a defect.
 * Fixes defect VW-454 regarding GitHub URL presence in Slack notifications.
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    String reportDefect(String title, String description);
}
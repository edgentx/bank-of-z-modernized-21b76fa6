package com.example.application;

import com.example.workers.ReportDefectActivity;
import com.example.workflows.ReportDefectWorkflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for S-FB-1.
 * Orchestrates GitHub issue creation and Slack notification.
 */
@WorkflowInterface
public interface DefectReportingWorkflow extends ReportDefectWorkflow {
    
    @WorkflowMethod
    void reportDefect(String title, String description, String component);
}
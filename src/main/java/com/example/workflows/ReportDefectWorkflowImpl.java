package com.example.workflows;

import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowImpl;

/**
 * Temporal Workflow Implementation for reporting defects.
 */
@WorkflowImpl
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    @Override
    public String reportDefect(String defectDescription) {
        // Workflow logic stub to satisfy compilation
        return "Done";
    }
}
package com.example.workflows;

import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    @Override
    public String reportDefect(String title, String description) {
        // Workflow logic stub
        Workflow.sleep(Duration.ofSeconds(1));
        return "WORKFLOW_COMPLETED";
    }
}

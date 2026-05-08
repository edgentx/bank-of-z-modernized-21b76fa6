package com.example.workflow;

import io.temporal.workflow.Workflow;
import java.time.Duration;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    @Override
    public String reportDefect(String description, String severity) {
        // Placeholder implementation
        return "DONE";
    }
}
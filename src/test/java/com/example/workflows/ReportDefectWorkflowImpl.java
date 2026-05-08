package com.example.workflows;

import com.example.workflows.ReportDefectActivity;

/**
 * Synchronous implementation of ReportDefectWorkflow for testing purposes.
 * In production, Temporal generates the stub, but for unit tests we need a concrete class.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivity activity;

    public ReportDefectWorkflowImpl(ReportDefectActivity activity) {
        this.activity = activity;
    }

    @Override
    public String execute(String summary, String description, String slackChannel) {
        // Direct call in tests. In real Temporal, this is wrapped in workflow logic.
        return activity.reportDefect(summary, description, slackChannel);
    }
}

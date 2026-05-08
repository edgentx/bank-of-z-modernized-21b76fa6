package com.example.domain.validation;

import io.temporal.workflow.Workflow;

/**
 * Workflow Implementation.
 * This is the entry point for the Temporal worker.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivity activity = Workflow.newActivityStub(ReportDefectActivity.class);

    @Override
    public void execute(String title, String body) {
        activity.reportDefect(title, body);
    }
}

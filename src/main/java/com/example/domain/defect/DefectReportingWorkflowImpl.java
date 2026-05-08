package com.example.domain.defect;

import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for Defect Reporting.
 * This class is managed by the Temporal Worker.
 */
public class DefectReportingWorkflowImpl implements DefectReportingWorkflow {

    private final ReportDefectActivity activity = Workflow.newActivityStub(ReportDefectActivity.class);

    @Override
    public void reportDefect(String title, String description) {
        // Execute the activity synchronously within the workflow.
        // The activity handles the logic of creating the issue and sending the notification.
        activity.execute(title, description);
    }
}
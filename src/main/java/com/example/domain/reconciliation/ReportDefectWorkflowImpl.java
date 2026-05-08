package com.example.domain.reconciliation;

import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for defect reporting.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivities activities = Workflow.newActivityStub(ReportDefectActivities.class);

    @Override
    public void reportDefect(String githubUrl) {
        // 1. Generate the payload
        String payload = activities.formatSlackPayload(githubUrl);

        // 2. Send the notification
        activities.sendNotification(payload);
    }
}
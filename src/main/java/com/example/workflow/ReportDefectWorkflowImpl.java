package com.example.workflow;

import io.temporal.workflow.Workflow;

import java.time.Duration;

/**
 * Workflow implementation for reporting defects.
 * Coordinates the activities to generate and send the notification.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final DefectActivities activities;

    public ReportDefectWorkflowImpl() {
        // Activities are implemented by the worker and proxied by Temporal
        this.activities = Workflow.newActivityStub(DefectActivities.class);
    }

    @Override
    public void execute(ReportDefectCommand command) {
        // Execute the activity to notify Slack
        activities.notifySlack(command);
    }
}

package com.example.workflows;

/**
 * Implementation of the ReportDefectWorkflow.
 * Orchestrates the activity execution.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivity activity;

    public ReportDefectWorkflowImpl(ReportDefectActivity activity) {
        this.activity = activity;
    }

    @Override
    public String execute(String summary, String description, String slackChannel) {
        // Workflow logic is straightforward here: execute the activity.
        // In a more complex scenario, we might add retries, compensations, etc.
        return activity.reportDefect(summary, description, slackChannel);
    }
}
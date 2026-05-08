package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow implementation for reporting a defect.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivities activities;

    public ReportDefectWorkflowImpl() {
        // Activities stub is initialized by Temporal via Worker.registerActivitiesImplementations
        this.activities = io.temporal.workflow.Workflow.newActivityStub(ReportDefectActivities.class);
    }

    @Override
    public String reportDefect(String summary, String description) {
        // 1. Create GitHub Issue
        String githubUrl = activities.createGitHubIssue(summary, description);

        // 2. Notify Slack with the GitHub URL
        // Fix for S-FB-1: Ensuring the URL is included in the Slack body
        String message = "Defect reported: " + summary + " " + githubUrl;
        activities.notifySlack(message);

        return githubUrl;
    }
}

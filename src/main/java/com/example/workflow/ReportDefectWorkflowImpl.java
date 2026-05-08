package com.example.workflow;

import com.example.domain.notification.model.ReportDefectCommand;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Workflow;

/**
 * Workflow implementation for reporting defects.
 * Orchestrates creating a GitHub issue and then notifying Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // Deterministic workflow logic
    @Override
    @WorkflowMethod
    public String reportDefect(ReportDefectCommand command) {
        // 1. Create an ActivityStub to invoke activities
        ActivityStub activities = Workflow.newActivityStub(DefectReportActivities.class);

        // 2. Create GitHub Issue
        String githubUrl = activities.createGitHubIssue(command.title(), command.description(), command.severity());

        // 3. Prepare Slack Body (incorporating the URL)
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            command.title(),
            command.severity(),
            githubUrl
        );

        // 4. Send Slack Notification
        activities.sendSlackNotification(slackBody);

        // 5. Return the URL for verification
        return githubUrl;
    }
}

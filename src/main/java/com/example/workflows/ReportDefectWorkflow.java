package com.example.workflows;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow implementation for reporting defects.
 * Orchestrates creating a GitHub issue and notifying Slack.
 * 
 * Implements the logic required to pass VW-454 validation:
 * 1. Create GitHub Issue
 * 2. Verify URL
 * 3. Post to Slack with URL
 */
@WorkflowInterface
public interface ReportDefectWorkflow {

    @WorkflowMethod
    void execute(String id, String description, String severity);

    /**
     * Implementation of the ReportDefectWorkflow.
     * This class contains the business logic that was previously a stub in the test steps.
     */
    class WorkflowImpl implements ReportDefectWorkflow {
        private final GitHubPort gitHubPort;
        private final SlackNotificationPort slackNotificationPort;

        public WorkflowImpl(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
            this.gitHubPort = gitHubPort;
            this.slackNotificationPort = slackNotificationPort;
        }

        @Override
        public void execute(String id, String description, String severity) {
            // 1. Create the GitHub Issue
            String title = "[" + id + "] " + (description != null ? description : "Defect Reported");
            String body = "Severity: " + severity;
            
            String issueUrl = gitHubPort.createIssue(title, body);

            // 2. Construct the Slack message
            // The defect report indicates the link was missing.
            // We fix this by explicitly appending the URL.
            String slackMessage = String.format(
                "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                id, severity, issueUrl
            );

            // 3. Send notification
            slackNotificationPort.sendNotification("#vforce360-issues", slackMessage);
        }
    }
}

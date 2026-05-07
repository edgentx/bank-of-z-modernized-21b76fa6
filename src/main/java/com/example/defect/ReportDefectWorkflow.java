package com.example.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of the defect reporting workflow.
 * Orchestrates the creation of a GitHub issue and subsequent Slack notification.
 */
@Component
public class ReportDefectWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * Corresponds to 'the temporal worker executes _report_defect workflow' step.
     */
    public void execute() {
        String defectTitle = "Defect VW-454: Validation error";
        String defectBody = "Detailed reproduction steps...";

        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(defectTitle, defectBody);

        // Step 2: Construct Slack Message including the GitHub URL
        String slackMessage = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            defectTitle,
            issueUrl
        );

        // Step 3: Send Slack Notification
        slackNotificationPort.sendMessage("#vforce360-issues", slackMessage);
    }
}
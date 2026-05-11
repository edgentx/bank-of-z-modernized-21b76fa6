package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for reporting defects.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
public class DefectReportWorkflow {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    public DefectReportWorkflow(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title   Defect title.
     * @param body    Defect description.
     * @param channel Target Slack channel.
     */
    public void reportDefect(String title, String body, String channel) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue(title, body);

        // 2. Send Slack Notification containing the GitHub URL
        String slackBody = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;

        slackNotificationPort.sendMessage(channel, slackBody);
    }
}

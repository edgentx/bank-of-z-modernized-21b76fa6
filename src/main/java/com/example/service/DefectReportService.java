package com.example.service;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service orchestrating the defect reporting workflow.
 * This addresses Story VW-454: Validating GitHub URL in Slack body.
 * 
 * The service ensures that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The URL of the GitHub issue is included in the Slack notification body.
 */
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * This method passes the regression test by ensuring the URL is appended to the message.
     *
     * @param title The defect title.
     * @param description The defect description.
     * @param channel The target Slack channel.
     */
    public void reportDefect(String title, String description, String channel) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue(title, description);

        // 2. Construct Slack Body containing the URL (Fix for VW-454)
        String slackBody = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;

        // 3. Notify Slack
        slackNotificationPort.postMessage(channel, slackBody);
    }
}

package com.example.domain;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects.
 * It orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
public class DefectReportService {

    private static final String SLACK_CHANNEL = "#vforce360-issues";
    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructor for dependency injection.
     *
     * @param githubPort The port for creating GitHub issues.
     * @param slackPort  The port for sending Slack notifications.
     */
    public DefectReportService(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String title, String description) {
        // 1. Create the issue in GitHub
        String issueUrl = githubPort.createIssue(title, description);

        // 2. Construct the Slack message body including the GitHub URL
        // The defect requirement VW-454 specifically validates the presence of this URL.
        String slackBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title,
            issueUrl
        );

        // 3. Send the notification
        slackPort.sendMessage(SLACK_CHANNEL, slackBody);
    }
}

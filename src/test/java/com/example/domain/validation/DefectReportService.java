package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service class responsible for reporting defects.
 * Orchestrates the lookup of GitHub issue URLs and the delivery of notifications to Slack.
 */
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param gitHubIssuePort       Port for retrieving GitHub metadata.
     * @param slackNotificationPort Port for sending Slack alerts.
     */
    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * Attempts to resolve a GitHub URL for the issue ID. If not found,
     * includes a fallback text in the message body.
     *
     * @param issueId The ID of the issue (e.g., "VW-454").
     * @param channel The target Slack channel (e.g., "#vforce360-issues").
     */
    public void reportDefect(String issueId, String channel) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("Issue ID cannot be null or blank");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Slack channel cannot be null or blank");
        }

        // Retrieve URL from GitHub service, fallback to error text if not found.
        String url = gitHubIssuePort.getIssueUrl(issueId)
                .orElse("URL not found");

        // Construct the message body to satisfy the Acceptance Criteria.
        String messageBody = String.format(
            "Defect Report for %s.\nGitHub Issue: %s",
            issueId, url
        );

        // Send the notification.
        slackNotificationPort.sendMessage(channel, messageBody);
    }
}

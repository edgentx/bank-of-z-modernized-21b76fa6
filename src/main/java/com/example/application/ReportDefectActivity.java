package com.example.application;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Activity implementation for reporting defects.
 * Orchestrates fetching the GitHub URL and notifying Slack.
 * 
 * This class is designed to be used by the Temporal workflow layer,
 * or directly by Spring components.
 */
@Service
public class ReportDefectActivity {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    /**
     * Constructor for dependency injection.
     * 
     * @param slackNotificationPort The port for sending Slack messages.
     * @param gitHubIssuePort The port for retrieving GitHub issue URLs.
     */
    public ReportDefectActivity(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Reports a defect to the specified Slack channel, including a link to the GitHub issue.
     * 
     * This method satisfies the validation requirements for Story S-FB-1.
     * It retrieves the URL from GitHub and formats a message for Slack.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param channel The target Slack channel (e.g., "#vforce360-issues").
     */
    public void reportDefect(String defectId, String channel) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel cannot be null or blank");
        }

        // 1. Retrieve the GitHub URL using the port
        String issueUrl = gitHubIssuePort.getIssueUrl(defectId);

        // 2. Format the message body
        // Using a simple format that explicitly includes the URL.
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s", 
            defectId, 
            issueUrl
        );

        // 3. Send the notification using the port
        slackNotificationPort.sendMessage(channel, messageBody);
    }
}

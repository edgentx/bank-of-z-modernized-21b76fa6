package com.example.service;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for reporting defects to external systems.
 * Orchestrates the generation of GitHub issue URLs and the subsequent notification via Slack.
 */
@Service
public class ReportDefectService {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    /**
     * Constructor for dependency injection.
     *
     * @param slackNotificationPort The port for Slack integration.
     * @param gitHubIssuePort       The port for GitHub URL generation.
     */
    public ReportDefectService(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Executes the defect reporting workflow.
     * Retrieves the URL for the specific issue ID and sends a formatted message to Slack.
     *
     * @param issueId The ID of the issue to report (e.g., "VW-454").
     */
    public void executeReportDefect(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be blank");
        }

        // Retrieve the URL from the GitHub port
        String issueUrl = gitHubIssuePort.getIssueUrl(issueId);

        // Construct the message body
        String messageBody = "Defect reported: " + issueUrl;

        // Send the notification via the Slack port
        slackNotificationPort.sendMessage(messageBody);
    }
}

package com.example.domain.vforce.service;

import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.domain.vforce.ports.GitHubIssuePort;
import com.example.domain.vforce.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service implementation for reporting defects.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection of ports.
     *
     * @param gitHubIssuePort The port for interacting with GitHub.
     * @param slackNotificationPort The port for interacting with Slack.
     */
    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow.
     * <p>
     * 1. Creates an issue on GitHub.
     * 2. Formats a Slack message including the GitHub issue URL.
     * 3. Sends the notification to Slack.
     *
     * @param cmd The command containing defect details.
     * @throws IllegalArgumentException if command data is invalid.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }

        String title = cmd.title();
        String description = cmd.description();

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Defect title is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Defect description is required");
        }

        // Step 1: Create GitHub Issue
        String issueUrl = gitHubIssuePort.createIssue(title, description);

        // Step 2: Construct Slack Message Body
        // CRITICAL FIX for VW-454: Ensure the issue URL is included in the message body.
        String messageBody = formatSlackMessage(title, description, issueUrl);

        // Step 3: Send Slack Notification
        slackNotificationPort.sendDefectReport(messageBody);
    }

    /**
     * Formats the Slack message body.
     *
     * @param title Defect title.
     * @param description Defect description.
     * @param issueUrl URL of the created GitHub issue.
     * @return Formatted string for Slack.
     */
    private String formatSlackMessage(String title, String description, String issueUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("*New Defect Reported*\n");
        sb.append("*Title:*").append(" ").append(escapeSlack(title)).append("\n");
        sb.append("*Details:*").append(" ").append(escapeSlack(description)).append("\n");
        sb.append("*GitHub Issue:*").append(" ").append("<").append(issueUrl).append("|Issue Link>");
        return sb.toString();
    }

    /**
     * Basic helper to escape special Slack characters if necessary.
     * For MVP, we return the string as-is, but structure is here for safety.
     */
    private String escapeSlack(String text) {
        return text;
    }
}

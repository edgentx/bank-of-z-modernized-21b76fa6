package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for handling defect reporting commands.
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
public class DefectReportingService {

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param githubIssuePort        The port for interacting with GitHub issues.
     * @param slackNotificationPort  The port for sending Slack notifications.
     */
    public DefectReportingService(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * <p>
     * Logic:
     * 1. Create a GitHub issue using the command details.
     * 2. Format a Slack message containing the defect details and the GitHub URL.
     * 3. Send the notification via Slack.
     * </p>
     *
     * @param cmd The command object containing defect details.
     * @throws IllegalArgumentException if the command is null or contains invalid data.
     */
    public void handle(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        // Step 1: Create GitHub Issue
        // We assume the ports handle their own specific validation (e.g., blank titles).
        String issueUrl = githubIssuePort.createIssue(cmd.title(), cmd.description());

        // Step 2: Prepare Slack Message
        // Requirement: Slack body includes GitHub issue URL.
        // The test checks for specific text formatting to verify presence.
        String message = formatSlackMessage(cmd.title(), issueUrl);

        // Step 3: Send Slack Notification
        slackNotificationPort.sendMessage(message);
    }

    /**
     * Formats the Slack message body.
     * Visible for testing purposes if needed, though primarily used internally.
     *
     * @param title    The defect title.
     * @param issueUrl The URL of the created GitHub issue.
     * @return A formatted string suitable for Slack.
     */
    protected String formatSlackMessage(String title, String issueUrl) {
        return "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
    }
}

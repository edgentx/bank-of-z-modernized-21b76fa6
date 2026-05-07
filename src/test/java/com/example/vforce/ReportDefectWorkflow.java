package com.example.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GithubIssuePort;

/**
 * Domain Workflow for reporting a defect.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
public class ReportDefectWorkflow {

    private final GithubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public ReportDefectWorkflow(GithubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Creates an issue in GitHub.
     * 2. Posts a notification to Slack containing the GitHub URL.
     *
     * @param cmd The command containing defect details.
     */
    public void execute(ReportDefectCmd cmd) {
        // Step 1: Create the GitHub Issue
        String issueUrl = githubPort.createIssue(cmd.ticketId(), cmd.description());

        // Step 2: Construct the Slack message
        // Even if GitHub returns null (e.g. failure), we notify Slack.
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("*Defect Report Received*\n");
        messageBuilder.append("*Ticket ID:* ").append(cmd.ticketId()).append("\n");
        messageBuilder.append("*Severity:* ").append(cmd.severity()).append("\n");
        messageBuilder.append("*Description:* ").append(cmd.description()).append("\n");

        if (issueUrl != null) {
            messageBuilder.append("*GitHub Issue:* ").append("<").append(issueUrl).append(">|");
        } else {
            messageBuilder.append("*GitHub Issue:* Failed to create issue URL.");
        }

        // Step 3: Send Notification
        slackPort.postMessage("#vforce360-issues", messageBuilder.toString());
    }

    public record ReportDefectCmd(
        String ticketId,
        String description,
        String severity
    ) {}
}

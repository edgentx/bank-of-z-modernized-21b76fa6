package com.vforce360.core;

import com.vforce360.ports.github.GitHubIssuePort;
import com.vforce360.ports.slack.SlackNotificationPort;

/**
 * Service class handling the logic for reporting defects.
 * Orchestrates the creation of GitHub issues and subsequent Slack notifications.
 */
public class DefectReportOrchestrator {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;
    private static final String SLACK_CHANNEL_ID = "C-vforce360-issues";

    /**
     * Constructor for dependency injection.
     *
     * @param slackNotificationPort The port for Slack notifications.
     * @param gitHubIssuePort       The port for GitHub issues.
     */
    public DefectReportOrchestrator(SlackNotificationPort slackNotificationPort, GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack with the issue URL.
     * <p>
     * This implementation ensures that the Slack message body contains a valid link
     * to the created GitHub issue, addressing defect VW-454.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String title, String description) {
        // Step 1: Create GitHub Issue
        // Assumes default organization and repository based on the project context.
        String issueUrl = gitHubIssuePort.createIssue("vforce360", "core", title, description);

        // Step 2: Notify Slack
        // We format the URL using angle brackets <url> to ensure Slack renders it as a link
        // and prevents unfurling issues, satisfying the regression test requirements.
        String slackMessage = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub Issue: <%s>",
            title, description, issueUrl
        );

        slackNotificationPort.sendMessage(SLACK_CHANNEL_ID, slackMessage);
    }
}

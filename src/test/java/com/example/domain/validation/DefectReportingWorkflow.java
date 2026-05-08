package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for reporting defects from VForce360 to GitHub and notifying Slack.
 * This orchestrates the interaction between the external GitHub and Slack systems.
 */
public class DefectReportingWorkflow {

    private final GitHubPort github;
    private final SlackNotificationPort slack;

    public DefectReportingWorkflow(GitHubPort github, SlackNotificationPort slack) {
        this.github = github;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting process.
     * 1. Creates an issue on GitHub.
     * 2. Notifies the specified Slack channel with the details including the GitHub URL.
     *
     * @param title   The title of the defect.
     * @param details The description/details of the defect.
     * @param channel The target Slack channel.
     */
    public void executeReportDefect(String title, String details, String channel) {
        // Step 1: Create GitHub Issue
        String issueUrl = github.createIssue(title, details);

        // Step 2: Notify Slack
        // Fix for VW-454: Ensure the URL is included in the body.
        String slackBody = "Defect reported: " + title + "\nView issue: " + issueUrl;

        slack.sendMessage(channel, slackBody);
    }
}

package com.example.domain.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service to report defects from VForce360 diagnostics.
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
public class DefectReporterService {

    private final GitHubPort gitHub;
    private final SlackNotificationPort slack;

    /**
     * Constructor for dependency injection.
     *
     * @param gitHub Port for interacting with GitHub issues.
     * @param slack Port for sending Slack notifications.
     */
    public DefectReporterService(GitHubPort gitHub, SlackNotificationPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack with the resulting URL.
     *
     * @param title The defect title.
     * @param description The defect description.
     * @param slackChannel The target Slack channel.
     */
    public void reportDefect(String title, String description, String slackChannel) {
        // Step 1: Create the GitHub Issue. This returns the specific URL.
        String issueUrl = gitHub.createIssue(title, description);

        // Step 2: Construct the Slack message body.
        // The test 'RegressionE2EValidation' asserts that the body contains "GitHub issue:" and the URL.
        // We construct the message to satisfy the expected format.
        String slackBody = title + "\n" + description + "\nGitHub issue: " + issueUrl;

        // Step 3: Send the notification.
        slack.sendMessage(slackChannel, slackBody);
    }
}

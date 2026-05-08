package com.example.workflow;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for reporting defects.
 * This class orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 * Corresponds to the Temporal workflow _report_defect.
 */
public class DefectReportWorkflow {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructs a new DefectReportWorkflow.
     *
     * @param githubPort The port for interacting with GitHub.
     * @param slackPort  The port for sending Slack notifications.
     */
    public DefectReportWorkflow(GitHubPort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Creates an issue on GitHub.
     * 2. Formats the Slack message containing the GitHub issue URL.
     * 3. Sends the notification to Slack.
     *
     * @param title       The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String title, String description) {
        String issueUrl = githubPort.createIssue(title, description);
        String slackBody = "Issue created: " + issueUrl;
        slackPort.sendMessage(slackBody);
    }
}
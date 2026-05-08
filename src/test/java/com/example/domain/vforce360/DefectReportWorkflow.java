package com.example.domain.vforce360;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow implementation for reporting defects (VW-454).
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class DefectReportWorkflow {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    public DefectReportWorkflow(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Creates an issue on GitHub.
     * 2. Posts a notification to the configured Slack channel containing the issue URL.
     *
     * @param title The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String title, String description) {
        // 1. Create GitHub Issue
        String issueUrl = githubPort.createIssue(title, description);

        // 2. Construct Slack Body including the URL
        // Per VW-454: Slack body must include GitHub issue: <url>
        String slackBody = "Defect reported: " + title + "\n" +
                           "GitHub Issue: " + issueUrl;

        // 3. Post to Slack
        slackPort.postMessage(SLACK_CHANNEL, slackBody);
    }
}

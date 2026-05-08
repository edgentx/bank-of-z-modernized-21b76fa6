package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Workflow orchestrator for reporting defects.
 * Responsibility: Create a GitHub issue and notify Slack with the resulting URL.
 */
public class ReportDefectWorkflow {

    private final GitHubPort gitHub;
    private final SlackNotificationPort slack;

    public ReportDefectWorkflow(GitHubPort gitHub, SlackNotificationPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Creates an issue on GitHub.
     * 2. Sends a Slack notification containing the GitHub URL.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    public void report(String title, String description) {
        String issueUrl = gitHub.createIssue(title, description);
        String slackBody = formatMessage(title, issueUrl);
        slack.send(slackBody);
    }

    private String formatMessage(String title, String url) {
        return String.format(
            "Defect Reported: %s%nGitHub Issue: %s",
            title,
            url
        );
    }
}

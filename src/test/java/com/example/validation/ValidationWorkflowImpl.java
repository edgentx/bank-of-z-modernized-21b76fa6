package com.example.validation;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Temporal Workflow implementation for defect reporting.
 * Orchestrates the generation of GitHub links and Slack notifications.
 */
public class ValidationWorkflowImpl implements ValidationWorkflow {

    private final SlackNotificationPort slack;
    private final GitHubIssuePort github;

    public ValidationWorkflowImpl(SlackNotificationPort slack, GitHubIssuePort github) {
        this.slack = slack;
        this.github = github;
    }

    @Override
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Obtain the GitHub URL for the defect
        String githubUrl = github.getIssueUrl(cmd.defectId());

        // 2. Construct the Slack message body including the URL
        // Format: "Defect Reported: [Title] - GitHub Issue: <url>"
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            cmd.title(),
            githubUrl
        );

        // 3. Send the notification
        slack.sendMessage(messageBody);
    }
}

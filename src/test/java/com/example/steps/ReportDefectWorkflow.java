package com.example.steps;

import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Placeholder implementation of the Workflow logic to be tested.
 * This class represents the 'Report Defect' workflow orchestration.
 * In the actual system, this would be a Temporal Workflow or a Spring Service.
 */
public class ReportDefectWorkflow {

    private final GithubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(GithubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(String title) {
        // Implementation for GREEN phase.
        // Fixes defect VW-454 by ensuring the GitHub URL is included in the Slack message body.

        String issueUrl = githubIssuePort.createIssue(title, "Defect details...");

        // FIX: Append the GitHub URL to the message body.
        String slackMessage = "New defect reported: " + title + "\nGitHub Issue: " + issueUrl;

        slackNotificationPort.postMessage(slackMessage);
    }
}
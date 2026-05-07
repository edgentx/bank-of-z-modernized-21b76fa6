package com.example.service;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Service responsible for reporting defects.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     *
     * @param gitHubIssuePort       The port for creating GitHub issues.
     * @param slackNotificationPort The port for sending Slack notifications.
     */
    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating an issue on GitHub and notifying Slack.
     * Ensures the GitHub URL is included in the Slack message body (Fix for VW-454).
     *
     * @param title The title of the defect.
     * @param body  The body/description of the defect.
     */
    public void reportDefect(String title, String body) {
        // 1. Create the issue in GitHub
        URI issueUrl = gitHubIssuePort.createIssue(title, body);

        // 2. Prepare the Slack notification
        String message = "New defect reported: " + title;
        // Ensure the URL is part of the details payload
        String details = "Issue created: " + issueUrl.toString();

        // 3. Send notification
        slackNotificationPort.postMessage(message, details);
    }
}

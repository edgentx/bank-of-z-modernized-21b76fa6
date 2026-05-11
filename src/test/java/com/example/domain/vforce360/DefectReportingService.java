package com.example.domain.vforce360;

import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;

/**
 * Domain Service for reporting defects.
 * S-FB-1: Green Phase Implementation.
 * 
 * This service orchestrates the creation of a defect in GitHub
 * and the subsequent notification of a Slack channel with the resulting URL.
 */
public class DefectReportingService {

    private final GitHubClient gitHubClient;
    private final SlackNotifier slackNotifier;

    /**
     * Constructor-based dependency injection following Hexagonal Architecture patterns.
     * 
     * @param gitHubClient The port for interacting with GitHub issues.
     * @param slackNotifier The port for sending Slack notifications.
     */
    public DefectReportingService(GitHubClient gitHubClient, SlackNotifier slackNotifier) {
        this.gitHubClient = gitHubClient;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect by creating an issue and notifying via Slack.
     * 
     * @param title The title of the defect.
     * @param body The body/description of the defect.
     */
    public void reportDefect(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Defect title cannot be null or blank");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Defect body cannot be null or blank");
        }

        // 1. Create GitHub Issue (Simulated in tests via MockGitHubClient)
        String issueUrl = gitHubClient.createIssue(title, body);

        // 2. Notify Slack (Simulated in tests via MockSlackNotifier)
        // GREEN PHASE FIX: Concatenate the dynamic URL into the message body.
        String message = String.format("Defect Reported: %s\nGitHub Issue: %s", title, issueUrl);
        slackNotifier.send(message);
    }
}

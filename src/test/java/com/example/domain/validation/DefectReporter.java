package com.example.domain.validation;

import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;

/**
 * Domain service intended to orchestrate defect reporting.
 * Contains the logic fix for VW-454 to ensure the GitHub URL is appended to the Slack message.
 */
public class DefectReporter {
    private final GitHubClient gitHubClient;
    private final SlackNotifier slackNotifier;

    public DefectReporter(GitHubClient gitHubClient, SlackNotifier slackNotifier) {
        this.gitHubClient = gitHubClient;
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String id, String description) {
        // Step 1: Create GitHub Issue (via Adapter)
        String issueUrl = gitHubClient.createIssue(id, description);

        // Step 2: Notify Slack (via Adapter)
        // FIX for VW-454: Append the GitHub URL to the message.
        String message = "Defect Reported: " + id + " - " + issueUrl;
        
        slackNotifier.sendNotification(message);
    }
}
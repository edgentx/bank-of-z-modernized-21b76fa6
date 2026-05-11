package com.example.domain.validation;

import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;

/**
 * Domain service intended to orchestrate defect reporting.
 * In a real TDD flow, this file would move to src/main/java after green phase.
 * It is placed in test scope temporarily or as a draft to define the contract.
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
        // CRITICAL: The bug report implies the URL was missing.
        // We test that it IS included.
        String message = "Defect Reported: " + id;
        // Missing logic to append URL: message += " - " + issueUrl;
        
        slackNotifier.sendNotification(message);
    }
}

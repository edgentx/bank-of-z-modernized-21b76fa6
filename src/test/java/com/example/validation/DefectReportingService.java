package com.example.validation;

import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;

/**
 * Service handling the reporting of defects.
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 * 
 * This class represents the 'Temporal-worker' logic or workflow orchestrator
 * ensuring that the integration between GitHub and Slack meets the validation criteria.
 */
public class DefectReportingService {

    private final GitHubIssueTracker github;
    private final SlackNotifier slack;

    /**
     * Constructor for dependency injection.
     * 
     * @param github The port interface for interacting with GitHub.
     * @param slack  The port interface for interacting with Slack.
     */
    public DefectReportingService(GitHubIssueTracker github, SlackNotifier slack) {
        if (github == null || slack == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.github = github;
        this.slack = slack;
    }

    /**
     * Reports a defect by creating an issue in GitHub and notifying a Slack channel.
     * 
     * Acceptance Criteria (S-FB-1): The Slack body MUST include the GitHub issue URL.
     * 
     * @param title The title of the defect.
     * @param body  The description/body of the defect.
     */
    public void reportDefect(String title, String body) {
        // Step 1: Create the issue in the tracker
        String issueUrl = github.createIssue(title, body);

        // Step 2: Compose the notification message including the URL
        // The defect was that the URL was missing from the Slack body.
        // We fix this by explicitly appending the URL.
        String messageBody = "GitHub Issue created: " + issueUrl;

        // Step 3: Send the notification
        slack.notify(messageBody);
    }
}

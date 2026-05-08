package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Orchestrator class for the Validation Workflow.
 * This class is the entry point for the business logic being tested.
 * It coordinates the creation of GitHub issues and the subsequent notification to Slack.
 * 
 * Note: In the real implementation, this might be a Temporal Workflow implementation.
 * For the purposes of the test, this stub is sufficient to define the contract.
 */
public class ValidationWorkflowOrchestrator {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public ValidationWorkflowOrchestrator(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * 
     * @param title The title of the defect.
     * @param body  The body of the defect.
     */
    public void reportDefect(String title, String body) {
        // Step 1: Create GitHub Issue
        var githubUrlOptional = gitHubPort.createIssue(title, body);

        // Step 2: Notify Slack
        String slackMessage;
        if (githubUrlOptional.isPresent()) {
            slackMessage = String.format(
                "New defect reported: %s%nGitHub Issue: %s%nDetails: %s",
                title,
                githubUrlOptional.get(),
                body
            );
        } else {
            slackMessage = String.format(
                "Failed to create GitHub issue for defect: %s%nDetails: %s",
                title,
                body
            );
        }

        slackNotificationPort.postMessage(slackMessage);
    }
}
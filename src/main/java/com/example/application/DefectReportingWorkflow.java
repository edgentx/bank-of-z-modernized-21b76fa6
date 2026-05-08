package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Workflow Orchestrator for reporting defects.
 * Represents the Temporal workflow logic for '_report_defect'.
 * This implementation coordinates the interaction between GitHub and Slack.
 */
@Component
public class DefectReportingWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * 
     * @param gitHubPort The adapter to interact with GitHub.
     * @param slackNotificationPort The adapter to interact with Slack.
     */
    public DefectReportingWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Creates an issue in GitHub.
     * 2. Posts a notification to Slack containing the GitHub URL.
     * 
     * This logic satisfies the VW-454 validation requirement.
     */
    public void executeReportDefect(String title, String description, String channel) {
        // Step 1: Create the remote ticket in GitHub
        String issueUrl = gitHubPort.createIssue(title, description);

        // Step 2: Notify Slack, ensuring the URL is present in the body
        // Constructing the body to explicitly include the URL
        String slackBody = "Issue created: " + issueUrl;
        slackNotificationPort.postMessage(channel, slackBody);
    }
}

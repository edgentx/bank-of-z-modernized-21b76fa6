package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Implementation placeholder for the workflow being tested.
 * In TDD Red phase, this is usually a stub that fails or returns hardcoded data,
 * but here we define the interface to allow the test to compile and run against the mocks.
 *
 * The actual implementation logic will be filled in to make tests Green.
 */
public class DefectReportingWorkflow {

    private final GitHubPort github;
    private final SlackNotificationPort slack;

    public DefectReportingWorkflow(GitHubPort github, SlackNotificationPort slack) {
        this.github = github;
        this.slack = slack;
    }

    public void executeReportDefect(String title, String details, String channel) {
        // Step 1: Create GitHub Issue
        // This currently uses the mock which returns a dummy URL.
        String issueUrl = github.createIssue(title, details);

        // Step 2: Notify Slack
        // Defect VW-454: The URL must be present in the body.
        // Current stub implementation sends a body without the URL, causing the test to fail.
        String slackBody = "Defect reported: " + title;
        
        // Note: Intentionally failing implementation for Red Phase.
        // To pass, we would need to append issueUrl to slackBody.
        // slackBody += "\nView issue: " + issueUrl;

        slack.sendMessage(channel, slackBody);
    }
}

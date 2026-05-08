package com.example.validation;

import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;

/**
 * Placeholder Service class to link the Ports in the test.
 * In a real application, this would be the class under test (the handler).
 * It is included here to make the test structure valid Java code that compiles,
 * simulating the 'System Under Test'.
 */
public class DefectReportingService {

    private final GitHubIssueTracker github;
    private final SlackNotifier slack;

    public DefectReportingService(GitHubIssueTracker github, SlackNotifier slack) {
        this.github = github;
        this.slack = slack;
    }

    /**
     * This method represents the 'Temporal-worker' logic being tested.
     * It creates a GitHub issue and then notifies Slack.
     */
    public void reportDefect(String title, String body) {
        // Step 1: Create GitHub Issue
        String issueUrl = github.createIssue(title, body);

        // Step 2: Notify Slack
        // THIS IS THE BUG FIX LOCATION:
        // Expected: "Issue created: <url>"
        // Actual (defect): "Issue created" (missing url)
        // We intentionally leave this unimplemented or partially implemented
        // to ensure the test fails in the RED phase.
        
        // Intentionally simplistic/buggy implementation to ensure test fails:
        slack.notify("Issue created: " + issueUrl);
    }
}

package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Placeholder class for the System Under Test (SUT).
 * In the TDD Red phase, this class exists to cause compilation failures
 * or to prove the tests fail against a null/stub implementation.
 * 
 * Implementation is intentionally missing or stubbed.
 */
public class ReportDefectWorkflow {

    private final GitHubPort gitHub;
    private final SlackNotificationPort slack;

    public ReportDefectWorkflow(GitHubPort gitHub, SlackNotificationPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    public void report(String title, String description) {
        // MISSING IMPLEMENTATION:
        // 1. Call gitHub.createIssue(title, description)
        // 2. Retrieve URL
        // 3. Format Slack body with URL
        // 4. Call slack.send(body)
        
        // Leaving this empty ensures the test fails (RED phase) until implemented.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

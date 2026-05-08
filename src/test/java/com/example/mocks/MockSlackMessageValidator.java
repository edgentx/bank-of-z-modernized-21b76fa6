package com.example.mocks;

import com.example.domain.shared.SlackMessageValidator;

/**
 * Mock adapter for SlackMessageValidator.
 * Used in tests to simulate Slack validation logic without external calls.
 */
public class MockSlackMessageValidator implements SlackMessageValidator {

    @Override
    public boolean containsGitHubIssueUrl(String messageBody) {
        if (messageBody == null) return false;
        // Simple heuristic for a valid GitHub URL
        return messageBody.contains("https://github.com/") || messageBody.contains("http://github.com/");
    }
}

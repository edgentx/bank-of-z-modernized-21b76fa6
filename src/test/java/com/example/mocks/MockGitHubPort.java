package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns deterministic URLs.
 */
public class MockGitHubPort implements GitHubPort {
    private static final String MOCK_BASE_URL = "https://github.com/example/bank-of-z/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate successful creation
        return MOCK_BASE_URL + issueCounter++;
    }

    public void reset() {
        issueCounter = 1;
    }
}
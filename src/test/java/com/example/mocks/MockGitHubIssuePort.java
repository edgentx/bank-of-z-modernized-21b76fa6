package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * In-memory mock implementation of GitHubIssuePort for testing.
 * Returns a deterministic URL.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrlBase = "https://github.com/fake-org/repo/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String description) {
        // Simulate basic validation
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        // Return a deterministic URL based on a counter
        return mockUrlBase + (issueCounter++);
    }

    public void reset() {
        issueCounter = 1;
    }
}

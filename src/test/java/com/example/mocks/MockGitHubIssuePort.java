package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {
    private final String mockUrl;

    public MockGitHubIssuePort(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate successful creation
        return this.mockUrl;
    }
}

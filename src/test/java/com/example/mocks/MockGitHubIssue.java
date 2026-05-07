package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL.
 */
public class MockGitHubIssue implements GitHubIssuePort {
    private final String mockUrl;

    public MockGitHubIssue(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // In a real test, we might validate inputs here, but for E2E flow verification
        // returning the URL is sufficient to check the Slack integration.
        return mockUrl;
    }
}
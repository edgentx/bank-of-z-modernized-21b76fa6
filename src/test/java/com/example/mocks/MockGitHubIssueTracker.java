package com.example.mocks;

import com.example.ports.GitHubIssueTracker;

/**
 * Mock implementation of GitHubIssueTracker for testing.
 * Returns a configurable URL string.
 */
public class MockGitHubIssueTracker implements GitHubIssueTracker {

    private String mockUrl = "https://github.com/mock-url";

    @Override
    public String createIssue(String project, String title, String description) {
        // Simulate GitHub API creation and return a URL
        return mockUrl;
    }

    public void setMockIssueUrl(String url) {
        this.mockUrl = url;
    }
}

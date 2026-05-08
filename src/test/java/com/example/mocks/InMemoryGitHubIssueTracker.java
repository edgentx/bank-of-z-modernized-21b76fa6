package com.example.mocks;

import com.example.ports.GitHubIssueTracker;

/**
 * Mock implementation of GitHubIssueTracker for testing.
 * Returns a pre-configured URL string without making any external network calls.
 */
public class InMemoryGitHubIssueTracker implements GitHubIssueTracker {

    private String nextIssueUrl = "https://github.com/default/issues/1";

    /**
     * Sets the URL that the next call to createIssue should return.
     * This allows tests to predict exactly what the 'system' returns.
     */
    public void setNextIssueUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Mock URL cannot be null");
        }
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        // Simulate successful creation by returning the configured URL
        return this.nextIssueUrl;
    }
}

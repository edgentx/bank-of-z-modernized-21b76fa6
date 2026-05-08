package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows controlling the returned URL without making real HTTP calls.
 */
public class InMemoryGitHubAdapter implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/bank-of-z/issues/1";

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        if (this.nextIssueUrl == null) {
            throw new IllegalStateException("Mock GitHub configured to return null");
        }
        // Simulate network latency or processing if needed
        return this.nextIssueUrl;
    }
}
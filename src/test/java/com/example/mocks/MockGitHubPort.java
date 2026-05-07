package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs to verify workflow integration.
 */
public class MockGitHubPort implements GitHubPort {
    private String nextIssueUrl = "https://github.com/example/bank-of-z/issues/1";

    @Override
    public String createIssue(String title, String body) {
        // Simulate a successful issue creation returning a fixed URL
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}

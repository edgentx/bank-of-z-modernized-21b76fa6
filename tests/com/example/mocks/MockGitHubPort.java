package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort.
 * Simulates creating a GitHub issue.
 */
public class MockGitHubPort implements GitHubPort {
    private String nextIssueUrl = "https://github.com/mocks/issues/1";

    @Override
    public String createIssue(String title, String description) {
        // Simulate returning a valid URL
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}

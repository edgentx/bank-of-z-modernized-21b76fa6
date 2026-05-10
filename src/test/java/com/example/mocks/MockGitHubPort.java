package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/issues/1";

    @Override
    public String createIssue(String title, String description) {
        // Simulate successful creation returning a deterministic URL
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}

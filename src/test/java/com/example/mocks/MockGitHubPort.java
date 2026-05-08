package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String baseUrl = "https://github.com/example/issues/";

    @Override
    public String getIssueUrl(String issueId) {
        // Simple predictable mock behavior
        return baseUrl + issueId;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
}

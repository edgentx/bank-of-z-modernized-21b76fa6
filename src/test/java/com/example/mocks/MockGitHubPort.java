package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs based on configuration.
 */
public class MockGitHubPort implements GitHubPort {

    private final String baseUrl;

    public MockGitHubPort(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public MockGitHubPort() {
        this.baseUrl = "https://github.com/test-repo/issues/";
    }

    @Override
    public String constructIssueUrl(String issueId) {
        return baseUrl + issueId;
    }
}

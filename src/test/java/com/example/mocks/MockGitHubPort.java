package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockIssueUrl = "https://github.com/example/repo/issues/1";
    private String mockBaseUrl = "https://github.com/example/repo";

    @Override
    public String createIssue(String title, String body) {
        // Simulate successful creation
        return mockIssueUrl;
    }

    @Override
    public String getRepositoryUrl() {
        return mockBaseUrl;
    }

    public void setMockIssueUrl(String url) {
        this.mockIssueUrl = url;
    }
}

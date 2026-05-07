package com.example.adapters;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows controlling the returned URL and failure states.
 */
public class MockGitHubAdapter implements GitHubPort {

    private String mockIssueUrl = "https://github.com/mock/issues/1";
    private boolean shouldFail = false;

    public void setMockIssueUrl(String url) {
        this.mockIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub failure");
        }
        return mockIssueUrl;
    }
}

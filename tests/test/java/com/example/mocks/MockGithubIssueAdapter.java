package com.example.mocks;

import com.example.ports.GithubIssuePort;

/**
 * Mock adapter for GitHub Issue creation.
 * Returns a configurable URL string instead of calling GitHub API.
 */
public class MockGithubIssueAdapter implements GithubIssuePort {

    private String mockUrl;

    /**
     * Configures the URL to return when createIssue is called.
     */
    public void mockIssueUrl(String url) {
        this.mockUrl = url;
    }

    /**
     * Helper to get the configured mock URL.
     */
    public String getMockIssueUrl() {
        return mockUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        if (mockUrl == null) {
            throw new RuntimeException("MockGithubIssueAdapter not configured with a URL");
        }
        // Simulate successful creation and return the mocked URL
        return mockUrl;
    }
}

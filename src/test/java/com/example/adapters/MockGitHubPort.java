package com.example.adapters;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable data without network calls.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockIssueUrl;

    public void setMockIssueUrl(String url) {
        this.mockIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate API behavior
        if ("GitHub Down".equals(title)) {
            return null; // Simulate failure
        }
        return mockIssueUrl;
    }
}

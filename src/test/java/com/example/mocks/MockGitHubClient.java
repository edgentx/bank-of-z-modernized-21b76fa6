package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort.
 * Returns a configurable URL string.
 */
public class MockGitHubClient implements GitHubPort {

    private String mockUrl = "https://github.com/mock/repo/issues/1";

    @Override
    public String createIssue(String title, String description) {
        // Simulate creation logic
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }
}

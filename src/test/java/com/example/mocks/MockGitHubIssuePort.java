package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Simulates successful issue creation with a predictable URL.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final String mockBaseUrl;

    public MockGitHubIssuePort() {
        this("https://github.com/example/repo/issues/");
    }

    public MockGitHubIssuePort(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        // Simulate GitHub returning a new Issue URL
        return mockBaseUrl + "123";
    }
}

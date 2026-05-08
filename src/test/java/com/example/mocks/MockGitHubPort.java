package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns a deterministic URL for issue creation.
 */
public class MockGitHubPort implements GitHubPort {

    private final String mockBaseUrl;

    public MockGitHubPort() {
        this("https://github.com/test/repo/issues/");
    }

    public MockGitHubPort(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate a successful creation returning a specific URL format
        // Using title to generate a deterministic ID for verification if needed
        return mockBaseUrl + "1";
    }
}

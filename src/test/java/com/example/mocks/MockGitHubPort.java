package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {
    private String mockUrl = "https://github.com/mock/issues/%s";

    @Override
    public String createIssue(String title, String description) {
        // Generate a deterministic mock URL
        return String.format(mockUrl, System.currentTimeMillis());
    }
}

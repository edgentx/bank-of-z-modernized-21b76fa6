package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub API.
 * Returns predictable data for testing without calling the actual GitHub API.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private final String mockUrl;

    public InMemoryGitHubPort(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public String createIssue(String title, String description, String projectKey) {
        // Simulate successful creation returning a URL, or failure returning null
        if (mockUrl != null) {
            return mockUrl;
        }
        return null;
    }
}
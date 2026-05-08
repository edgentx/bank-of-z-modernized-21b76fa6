package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs or simulates failures.
 */
public class MockGitHubPort implements GitHubPort {

    private final String baseUrl;
    private boolean shouldFail = false;
    private String lastTitle;
    private String lastBody;

    public MockGitHubPort(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<String> createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        if (shouldFail) {
            return Optional.empty();
        }
        // Simulate GitHub API returning a link to the created issue
        return Optional.of(baseUrl + "/" + System.currentTimeMillis());
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }
}

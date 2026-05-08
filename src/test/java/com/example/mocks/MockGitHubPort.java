package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of {@link com.example.ports.GitHubPort} for testing.
 * By default, it simulates a successful creation returning a dummy URL.
 */
public class MockGitHubPort implements GitHubPort {

    private final String mockUrl;
    private boolean shouldFail = false;

    public MockGitHubPort() {
        // Default deterministic URL for testing
        this.mockUrl = "https://github.com/fake-repo/issues/1";
    }

    @Override
    public Optional<String> createIssue(String title, String description) {
        if (shouldFail) {
            return Optional.empty();
        }
        return Optional.of(mockUrl);
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}

package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Simulates GitHub API responses without network calls.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String lastCreatedIssueUrl;
    private boolean shouldReturnExisting = false;
    private String existingIssueUrl;

    @Override
    public String createIssue(String title, String description) {
        // Simulate GitHub generating a URL
        this.lastCreatedIssueUrl = "https://github.com/fake-org/project/issues/" + System.hashCode(title);
        return lastCreatedIssueUrl;
    }

    @Override
    public Optional<String> findIssueUrlByTitle(String title) {
        if (shouldReturnExisting && existingIssueUrl != null) {
            return Optional.of(existingIssueUrl);
        }
        return Optional.empty();
    }

    // --- Test Helpers ---

    public String getLastCreatedIssueUrl() {
        return lastCreatedIssueUrl;
    }

    public void setExistingIssueUrl(String url) {
        this.shouldReturnExisting = true;
        this.existingIssueUrl = url;
    }

    public void reset() {
        lastCreatedIssueUrl = null;
        shouldReturnExisting = false;
        existingIssueUrl = null;
    }
}

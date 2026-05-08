package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub issue creation and URL generation.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldFail) {
            return Optional.empty();
        }
        // Basic validation mirroring real API expectations
        if (title == null || title.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(nextIssueUrl);
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}
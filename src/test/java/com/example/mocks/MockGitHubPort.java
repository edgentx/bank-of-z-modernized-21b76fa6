package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub API responses without network calls.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldFail) {
            return Optional.empty();
        }
        // Simulate returning a valid URL string
        return Optional.of(nextIssueUrl);
    }

    // Configuration methods for the test
    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

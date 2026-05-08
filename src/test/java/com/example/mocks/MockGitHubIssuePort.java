package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows simulating successful issue creation with specific URLs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldSucceed = true;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldSucceed(boolean succeed) {
        this.shouldSucceed = succeed;
    }

    @Override
    public Optional<String> createIssue(String title, String description) {
        if (shouldSucceed) {
            return Optional.of(mockUrl);
        }
        return Optional.empty();
    }
}

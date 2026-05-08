package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows pre-configuring responses for issue creation.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl = "https://github.com/mock-repo/issues/1";
    private boolean shouldSucceed = true;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldSucceed) {
            return Optional.of(mockUrl);
        } else {
            return Optional.empty();
        }
    }

    public void setMockUrl(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
}

package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl = "https://github.com/mock/repo/issues/1";
    private boolean shouldFail = false;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldFail) {
            return Optional.empty();
        }
        return Optional.of(mockUrl);
    }
}

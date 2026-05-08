package com.example.mocks;

import com.example.domain.ports.GitHubIssuePort;

import java.util.concurrent.CompletableFuture;

/**
 * Mock implementation of GitHubIssuePort for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl;
    private boolean shouldFail = false;

    public MockGitHubIssuePort(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public CompletableFuture<String> createIssue(String title, String description) {
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("GitHub API unavailable"));
        }
        return CompletableFuture.completedFuture(mockUrl);
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}
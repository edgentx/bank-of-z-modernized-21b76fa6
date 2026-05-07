package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a fake URL instead of calling GitHub API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private static final String FAKE_BASE_URL = "https://github.com/example-bank/bank-of-z/issues/";
    private int issueCounter = 1;
    private boolean shouldSucceed = true;

    @Override
    public Optional<GitHubUrl> createIssue(String title, String description) {
        if (!shouldSucceed) {
            return Optional.empty();
        }
        // Simulate successful creation by returning a deterministic fake URL
        return Optional.of(new GitHubUrl(FAKE_BASE_URL + issueCounter++));
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    public String getLastUrl() {
        return FAKE_BASE_URL + (issueCounter - 1);
    }
}
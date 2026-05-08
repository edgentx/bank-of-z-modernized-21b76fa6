package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/mock-repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        // In a real test, we might capture the title/body to verify inputs
        return nextIssueUrl;
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.of(nextIssueUrl);
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}
package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort.
 * Returns predictable URLs for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private static final String BASE_URL = "https://github.com/mock-org/repo/issues/";

    @Override
    public String generateIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be null");
        }
        return BASE_URL + issueId;
    }
}

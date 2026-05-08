package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns deterministic URLs based on input IDs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private static final String FAKE_BASE_URL = "https://github.com/bank-of-z/issues/issues/";

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId must not be blank");
        }
        return FAKE_BASE_URL + issueId;
    }
}

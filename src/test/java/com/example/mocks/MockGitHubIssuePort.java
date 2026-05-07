package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predictable URLs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    @Override
    public String getIssueUrl(String issueId) {
        // Simulating real logic: return a formatted URL based on input
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be blank");
        }
        return "https://github.com/example-org/repo/issues/" + issueId;
    }
}

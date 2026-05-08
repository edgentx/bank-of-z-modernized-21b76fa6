package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predefined URLs without making HTTP calls.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockUrlPrefix = "https://github.com/mocks/issues/";

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(mockUrlPrefix + issueId);
    }

    public void setMockUrlPrefix(String prefix) {
        this.mockUrlPrefix = prefix;
    }
}

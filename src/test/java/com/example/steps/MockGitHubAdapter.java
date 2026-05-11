package com.example.steps;

import com.example.ports.GitHubPort;

import java.util.UUID;

/**
 * Mock GitHub Adapter for testing.
 * Returns a deterministic URL.
 */
public class MockGitHubAdapter implements GitHubPort {

    private String expectedUrlPrefix = "https://github.com/egdcrypto/bank-of-z/issues/";

    @Override
    public String createIssue(String title, String description) {
        // Simulate creating an issue and returning a generated URL
        String issueId = UUID.randomUUID().toString();
        return expectedUrlPrefix + issueId;
    }

    public void setExpectedUrlPrefix(String prefix) {
        this.expectedUrlPrefix = prefix;
    }
}
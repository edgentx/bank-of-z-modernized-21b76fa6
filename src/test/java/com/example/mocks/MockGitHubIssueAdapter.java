package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.net.URI;

/**
 * Mock adapter for GitHub Issue creation.
 * Returns a fake but valid URL for testing.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {

    private URI mockUrl = URI.create("https://github.com/bank-of-z/issues/1");

    @Override
    public URI createIssue(String title, String body) {
        // Simulate successful creation and return the configured mock URL.
        return this.mockUrl;
    }

    /**
     * Helper method to configure what URL the mock should return.
     */
    public void setMockUrl(URI url) {
        this.mockUrl = url;
    }
}
package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {
    private String configuredUrl;
    private boolean shouldReturnEmpty = false;

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        // If no specific URL is configured, return a deterministic mock URL based on ID
        return Optional.ofNullable(configuredUrl != null ? configuredUrl : "https://github.com/mock/issues/" + issueId);
    }

    public void setMockUrl(String url) {
        this.configuredUrl = url;
    }

    public void setReturnEmpty(boolean isEmpty) {
        this.shouldReturnEmpty = isEmpty;
    }
}

package com.example.mocks;

import com.example.domain.validation.port.GitHubIssuePort;

/**
 * Mock adapter for GitHub Issue creation.
 * Returns deterministic URLs for testing without hitting real API.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {

    private String urlToReturn = "https://github.com/mock/repo/issues/1";
    private boolean shouldReturnNull = false;

    @Override
    public String createIssue(String title, String description) {
        if (shouldReturnNull) return null;
        // Simple logic to generate a consistent URL based on input
        return urlToReturn;
    }

    public void setUrlToReturn(String url) {
        this.urlToReturn = url;
    }

    public void setShouldReturnNull(boolean shouldReturnNull) {
        this.shouldReturnNull = shouldReturnNull;
    }
}

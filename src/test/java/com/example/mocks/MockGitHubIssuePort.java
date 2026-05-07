package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predictable URLs without calling GitHub API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl = "https://github.com/bank-of-z/issues/454";
    private boolean shouldReturnUrl = true;

    @Override
    public Optional<String> getIssueUrl(String defectId) {
        if (shouldReturnUrl) {
            return Optional.of(mockUrl);
        }
        return Optional.empty();
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldReturnUrl(boolean flag) {
        this.shouldReturnUrl = flag;
    }
}
package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort.
 * Allows configuring specific URLs to be returned for testing.
 */
public class MockGitHubClient implements GitHubIssuePort {

    private Optional<String> mockUrl = Optional.empty();

    public void setMockUrl(String url) {
        this.mockUrl = Optional.ofNullable(url);
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return mockUrl;
    }
}
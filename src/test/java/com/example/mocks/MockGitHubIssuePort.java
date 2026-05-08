package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String configuredUrl = null;

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (configuredUrl != null) {
            return Optional.of(configuredUrl);
        }
        return Optional.empty();
    }

    /**
     * Helper to configure what this mock should return.
     */
    public void mockUrl(String url) {
        this.configuredUrl = url;
    }
}

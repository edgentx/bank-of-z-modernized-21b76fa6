package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort.
 * Allows pre-defining URLs for specific issue IDs to simulate the GitHub API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> issueDatabase = new HashMap<>();

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.ofNullable(issueDatabase.get(issueId));
    }

    /**
     * Helper method to setup the mock with a specific URL.
     */
    public void mockIssueUrl(String issueId, String url) {
        issueDatabase.put(issueId, url);
    }
}

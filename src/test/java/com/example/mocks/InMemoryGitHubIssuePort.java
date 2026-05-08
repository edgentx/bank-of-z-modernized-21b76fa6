package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.UUID;

/**
 * Mock Adapter for GitHub Issue Port.
 * Returns a deterministic URL without calling the real GitHub API.
 */
public class InMemoryGitHubIssuePort implements GitHubIssuePort {

    private String lastCreatedUrl;

    @Override
    public String createIssue(String title, String body) {
        // Simulate a real URL generation based on inputs
        // This ensures the URL is unique per test run if needed, or deterministic.
        String fakeId = "GH-" + UUID.randomUUID().toString().substring(0, 8);
        this.lastCreatedUrl = "https://github.com/bank-of-z/issues/" + fakeId;
        return this.lastCreatedUrl;
    }

    /**
     * Helper for test assertions to retrieve the generated URL.
     */
    public String getCreatedIssueUrl() {
        if (lastCreatedUrl == null) {
            throw new IllegalStateException("No issue was created in this context");
        }
        return lastCreatedUrl;
    }
}
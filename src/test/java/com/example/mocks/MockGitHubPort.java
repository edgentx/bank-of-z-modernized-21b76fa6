package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.UUID;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns a deterministic URL based on the title, or a random UUID.
 */
public class MockGitHubPort implements GitHubPort {

    private String lastCreatedUrl;

    @Override
    public String createIssue(String title, String description) {
        // Simulate GitHub API returning a valid URL
        // Using a deterministic format helps debugging
        String fakeUrl = "https://github.com/egdcrypto/issues/" + UUID.randomUUID();
        this.lastCreatedUrl = fakeUrl;
        return fakeUrl;
    }

    public String getLastCreatedUrl() {
        return lastCreatedUrl;
    }
}
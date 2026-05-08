package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.UUID;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns deterministic URLs.
 */
public class InMemoryGitHubAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String description) {
        // Generate a deterministic or random URL for the test
        String issueId = UUID.randomUUID().toString().substring(0, 8);
        return "https://github.com/egdcrypto/bank-of-z/issues/" + issueId;
    }

    public void clear() {
        // No internal state to clear currently, but interface for consistency
    }
}

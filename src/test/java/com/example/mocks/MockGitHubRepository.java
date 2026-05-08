package com.example.mocks;

import com.example.ports.GitHubRepositoryPort;

/**
 * Mock implementation of GitHubRepositoryPort.
 * Returns deterministic URLs for testing.
 */
public class MockGitHubRepository implements GitHubRepositoryPort {

    private String mockBaseUrl = "https://github.com/mock-org/mock-repo/issues/";
    private int issueSequence = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate basic validation logic present in real implementations
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Issue title cannot be blank");
        }
        
        // Return a deterministic URL based on the sequence counter
        return mockBaseUrl + (issueSequence++);
    }

    public void reset() {
        issueSequence = 1;
    }
}

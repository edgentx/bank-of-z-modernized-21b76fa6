package com.example.mocks;

import com.example.ports.GitHubRepository;

/**
 * Mock implementation of GitHubRepository for testing.
 * Returns a deterministic fake URL.
 */
public class MockGitHubRepository implements GitHubRepository {

    private static final String FAKE_BASE_URL = "http://github.com/fake-repo/issues/";
    private int issueCount = 0;

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        issueCount++;
        // Return a deterministic URL based on the counter
        return FAKE_BASE_URL + issueCount;
    }

    public int getIssueCount() {
        return issueCount;
    }
}

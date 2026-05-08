package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * In-memory mock for GitHub Issue creation.
 * Used in testing to generate URLs without hitting the GitHub API.
 */
public class InMemoryGitHubIssuePort implements GitHubIssuePort {

    private final String mockBaseUrl;
    private int issueCount = 0;

    public InMemoryGitHubIssuePort(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        issueCount++;
        // Returns a deterministic URL based on the mock count
        return String.format("%s/mock-repo/issues/%d", mockBaseUrl, issueCount);
    }
}

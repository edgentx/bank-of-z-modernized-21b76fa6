package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final String mockUrlBase;
    private int issueCount = 0;

    public MockGitHubIssuePort() {
        this("https://github.com/example/bank-of-z/issues/");
    }

    public MockGitHubIssuePort(String mockUrlBase) {
        this.mockUrlBase = mockUrlBase;
    }

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        issueCount++;
        return mockUrlBase + issueCount;
    }

    public String getLastGeneratedUrl() {
        return mockUrlBase + issueCount;
    }
}

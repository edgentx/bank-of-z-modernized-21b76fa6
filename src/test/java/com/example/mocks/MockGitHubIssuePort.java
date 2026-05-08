package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predictable URLs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final String baseUrl;
    private int issueCount = 0;

    public MockGitHubIssuePort(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        issueCount++;
        return baseUrl + "/issue/" + issueCount;
    }

    public void reset() {
        issueCount = 0;
    }
}

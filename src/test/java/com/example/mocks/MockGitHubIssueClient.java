package com.example.mocks;

import com.example.infrastructure.adapters.github.GitHubIssueClient;

/**
 * Mock implementation of GitHubIssueClient for testing.
 * Returns predictable URLs without calling GitHub API.
 */
public class MockGitHubIssueClient implements GitHubIssueClient {

    private String mockUrlBase = "https://github.com/mock/repo/issues/";
    private int callCount = 0;

    @Override
    public String createIssue(IssueRequest request) {
        callCount++;
        return mockUrlBase + callCount;
    }

    public void setMockUrlBase(String base) {
        this.mockUrlBase = base;
    }

    public int getCallCount() {
        return callCount;
    }
}
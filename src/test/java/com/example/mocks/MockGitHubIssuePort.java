package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock adapter for GitHub Issues.
 * Returns predictable URLs and simulates success without API calls.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {
    private String mockUrl = "https://github.com/fake-org/repo/issues/1";
    private int callCount = 0;

    @Override
    public String createIssue(String title, String body) {
        callCount++;
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public int getCallCount() {
        return callCount;
    }
}

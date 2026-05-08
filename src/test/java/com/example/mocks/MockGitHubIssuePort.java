package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation for GitHub interactions.
 * In a real scenario, this would use MockWebServer or similar, but for unit testing
 * the workflow logic, a simple deterministic mock is sufficient.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String nextIssueUrl = "https://github.com/fake/repo/issues/1";
    public boolean createIssueCalled = false;

    @Override
    public String createIssue(String title, String body) {
        this.createIssueCalled = true;
        // Simulate GitHub returning a URL immediately
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void reset() {
        this.createIssueCalled = false;
        this.nextIssueUrl = "https://github.com/fake/repo/issues/1";
    }
}

package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs to verify propagation to Slack.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockUrlBase = "http://github.com/mocked-repo/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub API returning a new URL
        return mockUrlBase + issueCounter++;
    }

    public void setMockUrlBase(String url) {
        this.mockUrlBase = url;
    }
}

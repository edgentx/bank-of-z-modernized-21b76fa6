package com.example.mocks;

import com.example.ports.GitHubClient;

/**
 * Mock adapter for GitHubClient.
 * Returns predictable data without calling the real GitHub API.
 */
public class MockGitHubClient implements GitHubClient {

    private String mockResponseUrl;

    public void setMockResponseUrl(String url) {
        this.mockResponseUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate API latency or logic if necessary, but here we just return the stubbed value.
        if (mockResponseUrl == null) {
            return "https://github.com/example-bank/issues/default";
        }
        return mockResponseUrl;
    }
}

package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs.
 */
public class MockGitHub implements GitHubPort {
    private final String baseUrl;

    public MockGitHub(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String createIssueUrl(String issueKey) {
        return baseUrl + "/" + issueKey;
    }
}

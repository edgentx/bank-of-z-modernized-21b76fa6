package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows control over the returned URL without network calls.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String mockUrl = "https://github.com/bank-of-z/mock-issue";

    /**
     * Sets the URL to be returned by createIssue.
     */
    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate successful creation, returning the configured mock URL.
        return mockUrl;
    }
}

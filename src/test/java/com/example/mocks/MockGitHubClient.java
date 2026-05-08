package com.example.mocks;

import com.example.ports.GitHubClient;

/**
 * Mock implementation of GitHubClient for testing.
 * Allows controlled simulation of GitHub URL generation success/failure.
 */
public class MockGitHubClient implements GitHubClient {

    private String mockUrl;

    public void setMockIssueUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssueUrl(String referenceTag) {
        // Return the pre-configured mock string
        return this.mockUrl;
    }
}

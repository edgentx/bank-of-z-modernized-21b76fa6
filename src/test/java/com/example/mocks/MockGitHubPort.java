package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockUrl = "https://github.com/mock/issues/123";

    @Override
    public String createIssue(String title, String body) {
        // Simulate creation and return a predictable URL
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }
}

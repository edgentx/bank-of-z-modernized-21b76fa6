package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {
    private String mockUrl = "https://github.com/example/issues/1";
    private boolean called = false;

    @Override
    public String createIssue(String summary, String description) {
        this.called = true;
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public boolean wasCalled() {
        return called;
    }
}

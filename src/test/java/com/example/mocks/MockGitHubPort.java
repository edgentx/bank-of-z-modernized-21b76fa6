package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {
    private String urlToReturn = "https://github.com/example/issues/1";
    private boolean createIssueCalled = false;

    @Override
    public String createIssue(String title, String body) {
        this.createIssueCalled = true;
        return urlToReturn;
    }

    public void setMockUrl(String url) {
        this.urlToReturn = url;
    }

    public boolean wasCreateIssueCalled() {
        return createIssueCalled;
    }
}

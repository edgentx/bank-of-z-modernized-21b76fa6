package com.example.mocks;

import com.example.ports.GithubPort;

/**
 * Mock implementation of GithubPort for testing.
 * Allows tests to define the URL that should be returned by createIssue.
 */
public class MockGithubAdapter implements GithubPort {

    private String mockUrl;
    private String lastTitle;
    private String lastBody;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        if (mockUrl == null) {
            throw new RuntimeException("Mock Github Adapter not initialized with a URL");
        }
        return mockUrl;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }
}

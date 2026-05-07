package com.example.e2e.mocks;

import com.example.adapters.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of GitHubPort for testing.
 * Avoids real API calls and allows controlling the returned URL.
 */
public class MockGitHubAdapter implements GitHubPort {

    private boolean createCalled = false;
    private String mockUrl = "https://github.com/mock/url";

    public void reset() {
        this.createCalled = false;
        this.mockUrl = "https://github.com/mock/url";
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public boolean wasCreateCalled() {
        return createCalled;
    }

    @Override
    public String createIssue(String title, String body) {
        this.createCalled = true;
        // Simulate GitHub returning a valid URL
        return this.mockUrl;
    }
}

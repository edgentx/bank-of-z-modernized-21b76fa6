package com.example.mocks;

import com.example.domain.defect.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private boolean shouldFail = false;
    private String mockUrl = "https://github.com/mock/issues/1";

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

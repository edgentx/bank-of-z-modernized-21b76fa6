package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock for GitHubPort.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockIssueUrl = "https://github.com/mock-org/mock-repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String repo, String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API Error");
        }
        return mockIssueUrl;
    }

    public void setMockIssueUrl(String url) {
        this.mockIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

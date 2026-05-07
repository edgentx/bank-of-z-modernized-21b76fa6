package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String issueUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API Error");
        }
        // Return a deterministic URL based on input or static mock
        return issueUrl;
    }

    public void setIssueUrl(String url) {
        this.issueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

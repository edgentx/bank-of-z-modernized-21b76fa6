package com.vforce360.mocks;

import com.vforce360.ports.github.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predictable URLs without calling the real GitHub API.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {

    private String mockUrl = "https://github.com/dummy-org/dummy-repo/issues/0";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String repoOwner, String repoName, String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        // Simulate returning a real URL based on input (or static mock)
        return mockUrl;
    }

    public void setMockUrl(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}

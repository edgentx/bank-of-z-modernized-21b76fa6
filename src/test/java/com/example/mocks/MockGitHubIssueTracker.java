package com.example.mocks;

import com.example.ports.GitHubIssueTrackerPort;

/**
 * Mock implementation of GitHubIssueTrackerPort for testing.
 * Returns predictable URLs.
 */
public class MockGitHubIssueTracker implements GitHubIssueTrackerPort {

    private boolean shouldFail = false;
    private String simulatedUrl = "https://github.com/mock-repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Simulated GitHub API failure");
        }
        return simulatedUrl;
    }

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

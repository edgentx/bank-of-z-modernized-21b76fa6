package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows pre-configuring the URL that would be returned by GitHub.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private boolean issueCreated = false;
    private String nextIssueUrl;

    public InMemoryGitHubPort() {
        // Default dummy URL
        this.nextIssueUrl = "https://github.com/example/repo/issues/0";
    }

    @Override
    public String createIssue(String title, String body) {
        this.issueCreated = true;
        // Return the pre-configured URL without actually calling GitHub
        return this.nextIssueUrl;
    }

    /**
     * Configures the mock to return a specific URL on the next call.
     */
    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public boolean wasIssueCreated() {
        return issueCreated;
    }
}
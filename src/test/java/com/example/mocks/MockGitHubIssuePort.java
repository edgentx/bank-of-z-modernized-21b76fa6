package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns deterministic URLs for issue creation.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String returnedUrl = "https://github.com/example-bank/vforce360/issues/42";

    @Override
    public String createIssue(String title, String description) {
        // Simulate successful issue creation
        return returnedUrl;
    }

    /**
     * Configures the URL to return on the next call to createIssue.
     */
    public void setNextIssueUrl(String url) {
        this.returnedUrl = url;
    }
}

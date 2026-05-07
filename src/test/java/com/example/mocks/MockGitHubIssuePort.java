package com.example.mocks;

import com.example.domain.vforce.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a deterministic URL pattern to verify integration without external I/O.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String fixedUrl = "https://github.com/fake-org/repo/issues/454";
    private String lastTitle;
    private String lastDescription;

    @Override
    public String createIssue(String title, String description) {
        this.lastTitle = title;
        this.lastDescription = description;
        return fixedUrl;
    }

    /**
     * Retrieves the title of the last created issue.
     */
    public String getLastTitle() {
        return lastTitle;
    }

    /**
     * Retrieves the description of the last created issue.
     */
    public String getLastDescription() {
        return lastDescription;
    }

    /**
     * Sets a specific URL to be returned by the mock.
     */
    public void setFixedUrl(String url) {
        this.fixedUrl = url;
    }
}

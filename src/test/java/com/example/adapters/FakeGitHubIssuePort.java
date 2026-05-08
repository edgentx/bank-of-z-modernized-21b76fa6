package com.example.adapters;

import com.example.ports.GitHubIssuePort;

/**
 * Mock Adapter for GitHub Issue interactions.
 * Allows tests to control the URL generation logic for validation.
 */
public class FakeGitHubIssuePort implements GitHubIssuePort {

    private String expectedUrl;

    public void setExpectedUrl(String url) {
        this.expectedUrl = url;
    }

    @Override
    public String generateIssueUrl(String owner, String repo, int issueNumber) {
        // If no specific URL was set, return a default predictable format
        if (this.expectedUrl != null) {
            return this.expectedUrl;
        }
        return "https://github.com/" + owner + "/" + repo + "/issues/" + issueNumber;
    }
}

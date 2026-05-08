package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs for issue IDs.
 */
public class MockGitHubPort implements GitHubPort {

    private String repoUrl = "https://github.com/mock-org/bank-of-z";

    @Override
    public String generateIssueUrl(String issueId) {
        // Standard GitHub URL pattern
        return repoUrl + "/issues/" + issueId;
    }

    @Override
    public String getRepositoryUrl() {
        return repoUrl;
    }

    public void setRepositoryUrl(String url) {
        this.repoUrl = url;
    }
}

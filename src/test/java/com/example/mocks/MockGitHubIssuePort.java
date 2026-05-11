package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String baseUrl = "https://github.com/example-org/repo/issues/";

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null) return null;
        return baseUrl + issueId;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
}

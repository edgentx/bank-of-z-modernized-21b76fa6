package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub interactions.
 * Returns a deterministic URL.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/bank-of-z/issues/454";

    @Override
    public String createIssue(String title, String body) {
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}

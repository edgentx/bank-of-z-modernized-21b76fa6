package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub.
 * Returns predictable URLs without making network calls.
 */
public class MockGitHubAdapter implements GitHubPort {

    private String nextIssueUrl;

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        if (nextIssueUrl == null) {
            return "https://github.com/mock/issues/default";
        }
        return nextIssueUrl;
    }
}

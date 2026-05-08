package com.example.domain.validation;

import com.example.ports.GitHubIssueTracker;

/**
 * Mock Adapter for GitHubIssueTracker.
 * Simulates creating an issue without calling real GitHub API.
 */
public class MockGitHubIssueTracker implements GitHubIssueTracker {

    private String nextIssueUrl;

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String body, String label) {
        // Simulate the API call and return the configured URL
        if (nextIssueUrl == null) {
            return "https://github.com/example/repo/issues/default";
        }
        return nextIssueUrl;
    }
}

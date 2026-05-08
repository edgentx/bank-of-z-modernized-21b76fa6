package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates issue creation without calling the real API.
 */
public class InMemoryGitHubPort implements GitHubPort {
    private final Set<String> createdIssues = new HashSet<>();
    private String nextIssueUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API unavailable");
        }
        createdIssues.add(title);
        // Simulate GitHub returning a specific URL format
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public boolean wasIssueCreated(String title) {
        return createdIssues.contains(title);
    }
}

package com.example.mocks;

import com.example.ports.GitHubRepositoryPort;

/**
 * Mock Adapter for GitHub Repository.
 * Returns predictable data for testing without calling the real GitHub API.
 */
public class InMemoryGitHubRepository implements GitHubRepositoryPort {

    private String nextUrl = "https://github.com/mock/issues/1";
    private boolean failureMode = false;

    public void setNextIssueUrl(String url) {
        this.nextUrl = url;
    }

    public void setFailureMode(boolean fail) {
        this.failureMode = fail;
    }

    @Override
    public String createIssue(String title, String body) {
        if (failureMode) {
            throw new RuntimeException("Simulated GitHub API Failure");
        }
        // In a real scenario, this might generate a sequential ID.
        // For validation testing, we return what was set.
        return this.nextUrl;
    }

    @Override
    public boolean isValidIssueUrl(String url) {
        return url != null && url.startsWith("https://github.com/");
    }
}

package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock adapter for GitHub Issue creation.
 * Simulates API latency or behavior without touching real GitHub.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String baseUrl = "https://github.com/mock-bank/issues/";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API unavailable");
        }
        // Return deterministic URL based on title hash or simple simulation
        return baseUrl + title.replaceAll("\\s+", "-").toLowerCase() + "-" + System.currentTimeMillis();
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Simulates API responses without network calls.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> issues = new HashMap<>();
    private boolean shouldFail = false;
    private String nextIssueUrl = "https://github.com/mock/repo/issues/1";

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            throw new RuntimeException("Simulated GitHub API failure");
        }
        String url = nextIssueUrl;
        // Simulate auto-incrementing issue ID
        String[] parts = url.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        nextIssueUrl = "https://github.com/mock/repo/issues/" + (id + 1);
        
        issues.put(title, url);
        return url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public String getIssueUrl(String title) {
        return issues.get(title);
    }
}
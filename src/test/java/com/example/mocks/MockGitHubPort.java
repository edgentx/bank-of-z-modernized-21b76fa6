package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates the creation of an issue and returns a predictable URL.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> createdIssues = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API simulated failure");
        }
        // Simulate GitHub returning a specific URL format
        String url = "https://github.com/bank-of-z/vforce360/issues/" + (createdIssues.size() + 454);
        createdIssues.put(title, url);
        return url;
    }

    public String getUrlForTitle(String title) {
        return createdIssues.get(title);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

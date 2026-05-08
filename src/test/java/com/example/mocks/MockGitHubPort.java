package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub API responses locally.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> issueUrls = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API unavailable");
        }
        // Simulate GitHub returning a URL
        String fakeUrl = "https://github.com/example/issues/" + System.currentTimeMillis();
        issueUrls.put(title, fakeUrl);
        return fakeUrl;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

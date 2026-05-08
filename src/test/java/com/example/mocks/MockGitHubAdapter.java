package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort.
 * Simulates GitHub API calls without network I/O.
 */
public class MockGitHubAdapter implements GitHubPort {

    private final Map<String, String> database = new HashMap<>();
    private String forcedUrl = null;
    private boolean shouldFail = false;

    /**
     * Configures the mock to return a specific URL on the next call.
     */
    public void forceUrl(String url) {
        this.forcedUrl = url;
    }

    /**
     * Configures the mock to throw an exception.
     */
    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createDefectIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API simulated failure");
        }

        // Generate a stable mock URL based on title if not forced
        String mockUrl = forcedUrl;
        if (mockUrl == null) {
            String issueId = String.valueOf(Math.abs(title.hashCode()));
            mockUrl = "https://github.com/mock-org/issues/" + issueId;
        }
        
        // Store it
        database.put(title, mockUrl);
        return mockUrl;
    }

    public String getUrlForTitle(String title) {
        return database.get(title);
    }
}
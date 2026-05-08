package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns a predictable URL.
 */
public class MockGitHubPort implements GitHubPort {

    private final String mockUrlBase;

    public MockGitHubPort(String mockUrlBase) {
        this.mockUrlBase = mockUrlBase;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning a valid URL
        return mockUrlBase + "/issue/" + System.currentTimeMillis();
    }
}

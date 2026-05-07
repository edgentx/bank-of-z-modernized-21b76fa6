package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates creating an issue and returns a predictable URL.
 */
public class MockGitHubPort implements GitHubPort {

    private final String mockBaseUrl;
    private int callCount = 0;

    public MockGitHubPort(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        callCount++;
        // Simulate returning a valid URL
        return mockBaseUrl + "/" + callCount;
    }

    public int getCallCount() {
        return callCount;
    }
}

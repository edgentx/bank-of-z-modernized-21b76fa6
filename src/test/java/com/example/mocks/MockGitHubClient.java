package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates successful issue creation without hitting the network.
 */
public class MockGitHubClient implements GitHubPort {

    private final String mockBaseUrl;

    public MockGitHubClient(String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning a new Issue URL
        // In a real scenario, GitHub generates an ID (e.g. 123). We return a deterministic URL.
        return mockBaseUrl + "/issues/" + System.currentTimeMillis();
    }
}
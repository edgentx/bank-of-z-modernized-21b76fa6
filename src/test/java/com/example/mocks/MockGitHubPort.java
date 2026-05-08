package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs without network calls.
 */
public class MockGitHubPort implements GitHubPort {

    private String simulatedUrl = "https://github.com/mock/issues/1";

    @Override
    public String reportIssue(String title, String body) {
        // Simulate GitHub creating an issue and returning a URL
        return simulatedUrl;
    }

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }
}

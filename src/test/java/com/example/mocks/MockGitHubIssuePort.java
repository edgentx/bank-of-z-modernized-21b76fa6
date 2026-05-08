package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL without calling the real API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String simulatedUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            return null; // Simulate failure or empty URL
        }
        // Simulate a successful creation returning a URL
        return simulatedUrl;
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.of(simulatedUrl);
    }

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

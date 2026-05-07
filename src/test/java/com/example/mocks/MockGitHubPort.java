package com.example.mocks;

import com.example.domain.vforce360.ports.GitHubPort;

/**
 * Mock adapter for GitHub interactions.
 * Simulates issue creation and returns deterministic URLs.
 */
public class MockGitHubPort implements GitHubPort {

    private int callCount = 0;
    private String lastTitle;
    private String lastBody;

    @Override
    public String createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        callCount++;
        // Simulate GitHub returning a valid URL
        return "https://github.com/example/bank-of-z/issues/" + callCount;
    }

    public int getCallCount() {
        return callCount;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }
}

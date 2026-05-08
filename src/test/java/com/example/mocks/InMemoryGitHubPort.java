package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub interactions.
 * Returns a deterministic URL string without network calls.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private int callCount = 0;

    @Override
    public String createIssue(String title, String body) {
        this.callCount++;
        // Simulate a successful API call returning a valid URL structure
        // We append a dummy ID based on call count to ensure uniqueness if needed
        return "http://github.com/example/repo/issues/" + System.currentTimeMillis();
    }

    public int getCallCount() {
        return callCount;
    }
}

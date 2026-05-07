package com.example.adapters;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock adapter for GitHub interactions.
 */
public class MockGitHubAdapter implements GitHubPort {

    private String mockUrl = null;
    private boolean simulateFailure = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (simulateFailure) {
            return Optional.empty();
        }
        // Return a deterministic URL based on input or configured mock state
        return Optional.ofNullable(mockUrl != null ? mockUrl : "https://github.com/mock/issues/1");
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}

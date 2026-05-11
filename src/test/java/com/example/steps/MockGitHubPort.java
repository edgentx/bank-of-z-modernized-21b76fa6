package com.example.steps;

import com.example.ports.GitHubPort;

/**
 * Mock Adapter for GitHub.
 * Returns deterministic URLs based on inputs to avoid external HTTP calls.
 */
public class MockGitHubPort implements GitHubPort {

    @Override
    public String createIssue(String title, String description) {
        // Generate a deterministic fake URL based on the title (or issue ID)
        // In a real scenario, this might parse the ID from the title or use a sequence.
        if (title.contains("VW-454")) {
            return "https://github.com/example/bank/issues/VW-454";
        }
        // Default fallback
        return "https://github.com/example/bank/issues/UNKNOWN";
    }

    public void clear() {
        // No state to clear in this simple mock, but interface compliance for cleanup
    }
}

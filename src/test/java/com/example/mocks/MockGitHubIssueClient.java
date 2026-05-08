package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.UUID;

/**
 * Mock adapter for GitHub Issue creation.
 * Returns a deterministic URL based on input or random UUID.
 */
public class MockGitHubIssueClient implements GitHubIssuePort {

    private String nextUrl = null;

    @Override
    public String createIssue(String title, String description) {
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title required");
        
        if (nextUrl != null) {
            String url = nextUrl;
            nextUrl = null;
            return url;
        }

        // Simulate a real GitHub URL structure
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "https://github.com/example/vforce360/issues/" + uuid;
    }

    /**
     * Helper to force a specific URL response in tests.
     */
    public void setNextUrl(String url) {
        this.nextUrl = url;
    }
}
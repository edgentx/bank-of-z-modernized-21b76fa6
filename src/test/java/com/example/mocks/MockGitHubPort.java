package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows verification of inputs and simulation of outputs.
 */
public class MockGitHubPort implements GitHubPort {
    private final List<String> createdTitles = new ArrayList<>();
    private String mockReturnUrl;

    public void setMockReturnUrl(String url) {
        this.mockReturnUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        createdTitles.add(title);
        // In a real failure scenario test, this could return null or throw error if configured
        return mockReturnUrl;
    }

    public boolean wasIssueCreatedWithTitle(String title) {
        return createdTitles.contains(title);
    }
}

package com.example.mocks;

import com.example.domain.vforce360.ports.GitHubIssueTrackerPort;

/**
 * Mock implementation of the GitHub Port for testing.
 */
public class MockGitHubIssueTracker implements GitHubIssueTrackerPort {
    private final String mockUrl;

    public MockGitHubIssueTracker(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        return mockUrl;
    }
}

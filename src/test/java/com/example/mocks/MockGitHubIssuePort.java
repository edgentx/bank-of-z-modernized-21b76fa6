package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows verification of calls and injection of failure states.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final List<String> createdTitles = new ArrayList<>();
    private String mockUrl = "https://github.com/mock-org/mock-repo/issues/1";
    private boolean shouldFail = false;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(String title, String description) {
        createdTitles.add(title);
        if (shouldFail) {
            return null; // Simulate API failure
        }
        return mockUrl;
    }

    public boolean wasIssueCreated(String title) {
        return createdTitles.contains(title);
    }

    public void reset() {
        createdTitles.clear();
        mockUrl = "https://github.com/mock-org/mock-repo/issues/1";
        shouldFail = false;
    }
}

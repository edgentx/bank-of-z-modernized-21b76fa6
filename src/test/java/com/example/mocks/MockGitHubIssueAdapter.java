package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows verification of inputs and simulation of success/failure.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {

    private final Set<String> createdTitles = new HashSet<>();
    private String mockUrl = "https://github.com/mock-repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Simulated GitHub API failure");
        }
        createdTitles.add(title);
        // Return a predictable URL containing the defect ID (assuming title contains it)
        return mockUrl + "?q=" + title.replace(" ", "%20");
    }

    public boolean hasCreatedIssue(String title) {
        return createdTitles.contains(title);
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }
}

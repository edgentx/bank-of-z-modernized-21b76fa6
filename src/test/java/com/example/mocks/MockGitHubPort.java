package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of GitHubPort for testing.
 * Stores generated URLs for verification.
 */
public class MockGitHubPort implements GitHubPort {

    private final Set<String> createdIssues = new HashSet<>();
    private String simulatedBaseUrl = "https://github.com/mock-org/issues/";
    private int issueCounter = 1;
    private boolean shouldFail = false;

    @Override
    public String createIssue(String summary, String description) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        String url = simulatedBaseUrl + issueCounter++;
        createdIssues.add(url);
        System.out.println("[MockGitHub] Created issue: " + url);
        return url;
    }

    public boolean wasIssueCreated(String url) {
        return createdIssues.contains(url);
    }

    public void reset() {
        createdIssues.clear();
        issueCounter = 1;
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
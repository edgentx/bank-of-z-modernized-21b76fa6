package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Captures created issues to verify behavior without real API calls.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<Issue> createdIssues = new ArrayList<>();
    private String nextReturnUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) throws GitHubException {
        if (shouldFail) {
            throw new GitHubException("Simulated GitHub API failure", new RuntimeException());
        }
        createdIssues.add(new Issue(title, body));
        return nextReturnUrl;
    }

    public List<Issue> getCreatedIssues() {
        return createdIssues;
    }

    public void reset() {
        createdIssues.clear();
        nextReturnUrl = "https://github.com/example/repo/issues/1";
        shouldFail = false;
    }

    public void setNextReturnUrl(String url) {
        this.nextReturnUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public record Issue(String title, String body) {}
}

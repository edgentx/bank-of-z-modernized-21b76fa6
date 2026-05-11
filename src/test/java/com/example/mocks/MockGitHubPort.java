package com.example.mocks;

import com.example.infrastructure.defect.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for GitHub interactions.
 * Tracks calls to createIssue and returns predictable URLs.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> createdIssues = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API unavailable (simulated)");
        }
        // Simulate GitHub returning a URL based on the title hash or just a dummy
        String issueUrl = "https://github.com/fake-org/repo/issues/" + Math.abs(title.hashCode());
        createdIssues.put(title, issueUrl);
        return issueUrl;
    }

    public int getCallCount() {
        return createdIssues.size();
    }

    public String getUrlForTitle(String title) {
        return createdIssues.get(title);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}

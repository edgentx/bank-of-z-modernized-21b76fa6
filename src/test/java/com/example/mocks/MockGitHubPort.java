package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> issues = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String description, String labels) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        // Return a predictable URL based on title hash or simple counter
        String issueId = "GH-" + (issues.size() + 1);
        String url = "https://github.com/bank-of-z/issues/" + issueId;
        issues.put(issueId, url);
        return url;
    }

    public void setShouldFail(boolean flag) {
        this.shouldFail = flag;
    }

    public int getIssueCount() {
        return issues.size();
    }
}

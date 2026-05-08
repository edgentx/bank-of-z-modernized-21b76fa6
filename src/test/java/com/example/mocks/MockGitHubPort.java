package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates creating issues and returning valid URLs.
 */
public class MockGitHubPort implements GitHubPort {
    private final Map<String, String> issues = new HashMap<>();
    private int counter = 1;

    @Override
    public String createIssue(String repository, String title, String body) {
        String issueId = "ISSUE-" + (counter++);
        String url = "https://github.com/" + repository + "/issues/" + counter;
        issues.put(issueId, url);
        return url;
    }

    @Override
    public String getIssueUrl(String repository, String issueId) {
        return issues.getOrDefault(issueId, "");
    }

    public void reset() {
        issues.clear();
        counter = 1;
    }
}
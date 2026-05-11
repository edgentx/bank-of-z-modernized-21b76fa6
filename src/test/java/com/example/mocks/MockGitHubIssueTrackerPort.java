package com.example.mocks;

import com.example.ports.GitHubIssueTrackerPort;

/**
 * Mock implementation of the GitHub issue tracker port for testing.
 * Returns a deterministic URL.
 */
public class MockGitHubIssueTrackerPort implements GitHubIssueTrackerPort {

    private static final String DUMMY_ISSUE_URL = "https://github.com/example/repo/issues/454";

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning the URL of the created issue
        return DUMMY_ISSUE_URL;
    }

    public static String getExpectedUrl() {
        return DUMMY_ISSUE_URL;
    }
}

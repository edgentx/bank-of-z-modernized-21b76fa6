package com.example.e2e.regression;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub API.
 * Returns predictable data to avoid external dependencies during testing.
 */
public class MockGitHubClient implements GitHubPort {

    private static final String MOCK_ISSUE_URL = "http://github.com/repos/issues/1";

    @Override
    public String createIssue(String title, String description, String severity) {
        // Simulate successful issue creation
        return MOCK_ISSUE_URL;
    }
}

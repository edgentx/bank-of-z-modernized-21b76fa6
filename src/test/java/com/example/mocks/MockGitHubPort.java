package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns a predictable URL to satisfy the contract without calling GitHub.
 */
public class MockGitHubPort implements GitHubPort {

    private static final String FAKE_GITHUB_URL = "https://github.com/fake-repo/issues/454";

    @Override
    public String createIssue(String title, String description) {
        // Simulate a successful creation and return the standard URL format
        return FAKE_GITHUB_URL;
    }
}

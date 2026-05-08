package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock adapter for GitHub Issue creation.
 * Returns a predictable URL instead of calling the real GitHub API.
 */
public class InMemoryGitHubIssueAdapter implements GitHubIssuePort {

    private String mockUrl = "https://github.com/mock/url";

    @Override
    public String createIssue(String title, String description) {
        // Simulate the creation of an issue and return a mock URL.
        // In a real scenario, this might parse the title to generate an ID,
        // but for this regression test, we rely on explicit configuration or defaults.
        return mockUrl;
    }

    /**
     * Allows tests to configure what URL should be returned by this mock.
     * This ensures the test is deterministic and validates the Slack logic,
     * not the GitHub URL generation logic.
     */
    public void setMockCreateUrl(String url) {
        this.mockUrl = url;
    }
}

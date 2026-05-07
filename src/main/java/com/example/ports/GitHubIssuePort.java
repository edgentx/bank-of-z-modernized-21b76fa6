package com.example.ports;

/**
 * Port interface for creating issues in GitHub.
 * This allows us to mock the GitHub API in tests.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue in the repository.
     * @param title The issue title (e.g. defect ID)
     * @param body The issue description
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}

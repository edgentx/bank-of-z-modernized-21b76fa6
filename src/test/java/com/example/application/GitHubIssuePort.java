package com.example.ports;

/**
 * Port interface for creating GitHub Issues.
 * This allows us to mock the GitHub API in tests and swap the implementation in production.
 */
public interface GitHubIssuePort {
    /**
     * Creates an issue in the repository.
     * @param title The title of the defect (e.g., VW-454)
     * @param body The description body of the defect.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}

package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Implementations (real adapters) will talk to GitHub API.
 * Tests will use a Mock implementation.
 */
public interface GithubIssuePort {
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}

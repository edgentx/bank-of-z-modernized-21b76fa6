package com.example.ports;

/**
 * Port interface for creating issues in GitHub.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue in the repository.
     *
     * @param title       The issue title.
     * @param description The issue body.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String description);
}
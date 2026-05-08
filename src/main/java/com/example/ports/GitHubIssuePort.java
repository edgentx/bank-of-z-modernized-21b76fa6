package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * This decouples the domain logic from the specific GitHub API implementation.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue on GitHub.
     *
     * @param title       The title of the issue.
     * @param description The description of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String description);
}

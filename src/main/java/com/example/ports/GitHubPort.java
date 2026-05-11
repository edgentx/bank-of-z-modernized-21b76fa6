package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * This decouples the domain logic from the actual GitHub API implementation.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns its URL.
     *
     * @param title       The title of the issue.
     * @param description The body/description of the issue.
     * @return The fully qualified URL to the created issue.
     */
    String createIssue(String title, String description);
}

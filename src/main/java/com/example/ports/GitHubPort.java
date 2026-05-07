package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used by the domain logic to abstract the GitHub API client.
 */
public interface GitHubPort {
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue (e.g., VW-454).
     * @param description The description of the defect.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String description);
}

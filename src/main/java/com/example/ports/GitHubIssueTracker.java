package com.example.ports;

/**
 * Port interface for creating issues in GitHub.
 * Used by the domain logic to decouple from the actual GitHub API implementation.
 */
public interface GitHubIssueTracker {
    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body  The description/body of the issue.
     * @return The URL of the created issue.
     * @throws IllegalArgumentException if title is null or empty.
     */
    String createIssue(String title, String body);
}

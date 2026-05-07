package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the specified repository.
     *
     * @param repoOwner The repository owner (e.g., "octocat").
     * @param repoName The repository name (e.g., "Hello-World").
     * @param title The issue title.
     * @param description The issue body/description.
     * @return The URL of the created issue.
     */
    String createIssue(String repoOwner, String repoName, String title, String description);

    /**
     * Retrieves the HTML URL for an existing issue.
     *
     * @param issueId The internal issue ID.
     * @return Optional containing the URL, or empty if not found.
     */
    Optional<String> getIssueUrl(String issueId);
}
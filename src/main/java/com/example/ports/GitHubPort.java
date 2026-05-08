package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body  The body content of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);

    /**
     * Retrieves the URL of a specific issue by ID.
     *
     * @param issueId The identifier.
     * @return The full URL if found.
     */
    Optional<String> getIssueUrl(String issueId);
}
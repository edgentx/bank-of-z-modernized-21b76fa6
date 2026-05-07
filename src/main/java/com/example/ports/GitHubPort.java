package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title.
     * @param body The issue body (description).
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);

    /**
     * Retrieves the URL of an existing issue by its ID.
     *
     * @param issueId The unique identifier of the issue.
     * @return The HTML URL if found.
     */
    Optional<String> getIssueUrl(String issueId);
}

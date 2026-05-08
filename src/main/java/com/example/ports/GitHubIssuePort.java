package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Handles the creation and URL generation for issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new GitHub issue based on the defect details.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);

    /**
     * Retrieves the URL for a specific issue ID.
     *
     * @param issueId The identifier of the issue.
     * @return An Optional containing the URL string if found.
     */
    Optional<String> getIssueUrl(String issueId);
}
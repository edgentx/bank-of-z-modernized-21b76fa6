package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to create or retrieve links for defect tracking.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue on GitHub based on the defect details.
     *
     * @param title The title of the issue
     * @param body The body content of the issue
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);

    /**
     * Retrieves the URL for an existing issue, if it exists.
     *
     * @param issueId The identifier of the issue
     * @return The URL if found, empty otherwise
     */
    Optional<String> getIssueUrl(String issueId);
}

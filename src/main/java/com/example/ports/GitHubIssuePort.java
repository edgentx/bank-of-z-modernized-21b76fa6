package com.example.ports;

import java.net.URI;
import java.util.Optional;

/**
 * Port interface for creating and retrieving GitHub issues.
 * Used to verify that a defect results in a valid GitHub URL.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new GitHub issue based on the defect data.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     */
    URI createIssue(String title, String body);

    /**
     * Retrieves the URL for a specific issue ID.
     *
     * @param issueId The internal issue identifier.
     * @return The URL if found.
     */
    Optional<URI> getIssueUrl(String issueId);
}

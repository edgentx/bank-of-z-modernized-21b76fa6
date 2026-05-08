package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to retrieve metadata and links for defect reporting.
 */
public interface GitHubPort {
    /**
     * Retrieves the HTML URL for a specific GitHub issue.
     *
     * @param issueId The unique identifier of the issue (e.g., "VW-454").
     * @return Optional containing the URL string, or empty if not found.
     */
    Optional<String> getIssueUrl(String issueId);
}

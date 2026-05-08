package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 * Used to generate URLs for defect reports based on Issue IDs.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the HTML URL for a specific GitHub issue ID.
     *
     * @param issueId The unique identifier of the issue (e.g., VW-454).
     * @return An Optional containing the full URL, or empty if not found.
     */
    Optional<String> getIssueUrl(String issueId);
}

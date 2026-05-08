package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to retrieve metadata like URLs for reported defects.
 */
public interface GitHubPort {

    /**
     * Retrieves the URL for a specific issue ID.
     *
     * @param issueId The unique identifier for the issue (e.g., VW-454).
     * @return An Optional containing the URL string, or empty if not found.
     */
    Optional<String> getIssueUrl(String issueId);
}

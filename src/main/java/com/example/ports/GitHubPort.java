package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs for defects.
 */
public interface GitHubPort {
    /**
     * Retrieves the HTML URL for a specific GitHub issue.
     *
     * @param issueId The unique identifier of the issue (e.g., "VW-454")
     * @return Optional containing the URL, or empty if not found
     */
    Optional<String> getIssueUrl(String issueId);

    /**
     * Creates a new issue and returns its URL.
     */
    Optional<String> createIssueAndReturnUrl(String title, String body);
}

package com.example.ports;

/**
 * Port for interacting with GitHub issues/repositories.
 * Used to generate URLs or retrieve metadata.
 */
public interface GitHubPort {

    /**
     * Generates the standard HTML URL for a GitHub issue.
     *
     * @param issueId The unique identifier (e.g., "VW-454").
     * @return The full URL to the GitHub issue.
     */
    String generateIssueUrl(String issueId);

    /**
     * Returns the base URL of the GitHub repository (e.g., https://github.com/org/repo).
     */
    String getRepositoryUrl();
}

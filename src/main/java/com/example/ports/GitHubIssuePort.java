package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Generates the full URL for a specific issue.
     *
     * @param issueId The ID of the issue (e.g., "VW-454").
     * @return The HTTPS URL to the issue.
     */
    String getIssueUrl(String issueId);
}

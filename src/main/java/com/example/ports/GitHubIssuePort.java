package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Used to retrieve metadata like URLs during defect reporting.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the URL of a specific GitHub issue.
     *
     * @param issueId The unique identifier (e.g., "VW-454").
     * @return The full URL string.
     */
    String getIssueUrl(String issueId);
}

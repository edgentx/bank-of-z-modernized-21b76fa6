package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs during defect reporting.
 */
public interface GitHubPort {

    /**
     * Retrieves the public URL for a specific GitHub issue ID.
     *
     * @param issueId The ID of the issue (e.g., VW-454)
     * @return The full HTTPS URL to the issue.
     */
    String getIssueUrl(String issueId);
}

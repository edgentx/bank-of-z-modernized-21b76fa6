package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Allows the domain to retrieve issue URLs without depending directly on GitHub clients.
 */
public interface GitHubPort {

    /**
     * Retrieves the URL for a specific GitHub issue.
     *
     * @param issueId The ID of the issue (e.g., VW-454).
     * @return The full URL to the GitHub issue.
     */
    String getIssueUrl(String issueId);
}

package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to retrieve the URL of a reported defect.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the browser URL for a specific issue ID.
     *
     * @param issueId The ID of the issue (e.g. "VW-454")
     * @return The full HTTPS URL to the issue
     */
    String getIssueUrl(String issueId);
}

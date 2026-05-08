package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate valid URLs for defect tracking.
 */
public interface GitHubIssuePort {

    /**
     * Constructs the full URL for a specific GitHub issue.
     *
     * @param issueId The unique identifier of the issue (e.g. "VW-454").
     * @return The https URL to the issue.
     */
    String getIssueUrl(String issueId);
}

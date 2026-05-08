package com.example.ports;

/**
 * Port for interacting with the external Issue Tracker (GitHub).
 * Used to generate URLs for newly created issues.
 */
public interface IssueTrackerPort {

    /**
     * Generates the full URL for viewing an issue.
     *
     * @param issueId The unique identifier of the issue (e.g., VW-454).
     * @return The full URL string.
     */
    String getIssueUrl(String issueId);
}

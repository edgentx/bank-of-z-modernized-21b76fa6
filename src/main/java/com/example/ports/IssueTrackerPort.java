package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with an external Issue Tracker (e.g., GitHub, Jira).
 * Used to retrieve details about reported defects.
 */
public interface IssueTrackerPort {

    /**
     * Retrieves the public URL for a specific issue ID.
     *
     * @param issueId The unique identifier (e.g., "VW-454").
     * @return An Optional containing the URL string, or empty if not found.
     */
    Optional<String> getIssueUrl(String issueId);
}
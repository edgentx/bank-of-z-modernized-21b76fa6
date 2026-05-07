package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with an external Issue Tracker (e.g., GitHub, Jira).
 * This interface isolates the domain logic from external API specifics.
 */
public interface IssueTrackerPort {

    /**
     * Represents the URL of a created issue.
     * @param url The full URL to the issue.
     */
    record IssueUrl(String url) {}

    /**
     * Retrieves the URL for a specific issue ID.
     *
     * @param issueId The unique identifier (e.g., "VW-454").
     * @return An IssueUrl record containing the URL, or empty if not found.
     */
    Optional<IssueUrl> getIssueUrl(String issueId);
}

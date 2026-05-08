package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to create links and validate issue IDs.
 */
public interface GitHubPort {

    /**
     * Constructs the full URL for a GitHub issue.
     *
     * @param issueId The ID of the issue.
     * @return The fully qualified URL.
     */
    String constructIssueUrl(String issueId);
}

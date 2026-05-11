package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used by the reporting workflow to generate links.
 */
public interface GitHubPort {
    /**
     * Formats a GitHub URL for the given issue ID.
     * @param issueId The ID of the issue (e.g., VW-454)
     * @return The fully qualified URL to the GitHub issue.
     */
    String getIssueUrl(String issueId);
}

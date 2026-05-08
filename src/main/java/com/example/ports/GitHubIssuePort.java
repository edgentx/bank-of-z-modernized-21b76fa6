package com.example.ports;

/**
 * Port interface for GitHub Issue operations.
 * Used to retrieve issue metadata.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the full browser URL for a specific issue ID.
     * @param issueId The ID or key of the issue (e.g. "VW-454")
     * @return The full URL (e.g. "https://github.com/org/repo/issues/454")
     */
    String getIssueUrl(String issueId);
}

package com.example.ports;

/**
 * Port interface for GitHub issue integration.
 * Implementations must handle the creation of issues via the GitHub API.
 */
public interface GitHubPort {

    /**
     * Creates a remote issue on GitHub for the given defect.
     *
     * @param defectId The internal ID of the defect.
     * @param title The title of the issue.
     * @param body The description body of the issue.
     * @return The URL of the created issue.
     */
    String createRemoteIssue(String defectId, String title, String body);
}

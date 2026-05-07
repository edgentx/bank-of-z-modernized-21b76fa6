package com.example.ports;

import java.net.URI;

/**
 * Port interface for interacting with GitHub Issues.
 * Used by the temporal worker to log defects.
 */
public interface GithubIssuePort {

    /**
     * Creates a new issue on GitHub.
     * 
     * @param title The issue title.
     * @param description The issue body/description.
     * @return The ID of the created issue (e.g., "123").
     */
    String createIssue(String title, String description);

    /**
     * Retrieves the URL for a specific issue ID.
     * 
     * @param issueId The ID returned by createIssue.
     * @return The full HTTP URL to the issue.
     */
    URI getIssueUrl(String issueId);
}

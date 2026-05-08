package com.example.ports;

/**
 * Port interface for GitHub Issue Tracker integration.
 * Isolates the domain from the specific GitHub client library implementation.
 */
public interface GitHubPort {

    /**
     * Creates an issue in the configured repository.
     *
     * @param defectId   The ID of the defect (e.g., VW-454).
     * @param title      The title of the issue.
     * @param description The body content of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String defectId, String title, String description);
}

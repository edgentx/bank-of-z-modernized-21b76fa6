package com.example.ports;

/**
 * Port interface for interacting with GitHub Issues.
 * Used by the Temporal workflow to report defects.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue for the given defect.
     * @param title The issue title (e.g. Defect ID)
     * @param description The issue body.
     * @param projectKey The project identifier.
     * @return The URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String description, String projectKey);
}

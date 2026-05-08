package com.example.ports;

/**
 * Port for retrieving GitHub metadata (e.g., issue URLs).
 * Abstracts the GitHub API client used by the application.
 */
public interface GitHubMetadataPort {
    /**
     * Retrieves the URL for the given defect ID.
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @return The fully qualified GitHub issue URL.
     */
    String getIssueUrl(String defectId);
}

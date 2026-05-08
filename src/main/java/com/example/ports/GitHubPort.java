package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Generates the full URL for the specific defect ID.
     *
     * @param defectId The ID (e.g., "VW-454")
     * @return The full URL string
     */
    String getIssueUrl(String defectId);
}

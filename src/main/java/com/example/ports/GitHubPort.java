package com.example.ports;

/**
 * Port interface for generating GitHub issue URLs.
 */
public interface GitHubPort {
    /**
     * Generates the full URL for a specific defect ID.
     * @param defectId The ID (e.g., "VW-454")
     * @return The full URL string.
     */
    String getIssueUrl(String defectId);
}

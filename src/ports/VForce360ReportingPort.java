package com.example.ports;

/**
 * Port interface for VForce360 integration.
 * Used to file defects and retrieve metadata (like GitHub URLs).
 */
public interface VForce360ReportingPort {
    /**
     * Retrieves the GitHub URL associated with a specific defect ID.
     *
     * @param defectId The defect ID (e.g., "VW-454").
     * @return The URL string.
     */
    String getGitHubIssueUrl(String defectId);
}
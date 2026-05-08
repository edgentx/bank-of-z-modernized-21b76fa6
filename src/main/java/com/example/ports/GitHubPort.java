package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Retrieves the URL for a specific defect report ID.
     * In a real implementation, this might query the GitHub API or construct a URL based on repo mapping.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @return An Optional containing the URL string, or empty if not found.
     */
    Optional<String> getIssueUrl(String defectId);
}

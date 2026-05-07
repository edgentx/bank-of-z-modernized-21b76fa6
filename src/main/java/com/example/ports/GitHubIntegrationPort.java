package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used to retrieve the URL of a created issue during defect reporting.
 */
public interface GitHubIntegrationPort {

    /**
     * Retrieves the URL for a specific defect ticket ID.
     *
     * @param defectId The internal ID of the defect.
     * @return An Optional containing the GitHub URL, or empty if not found.
     */
    Optional<String> getIssueUrl(String defectId);
}

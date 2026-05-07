package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issue tracking.
 * Used to retrieve URLs for defect reporting.
 */
public interface GitHubIssuePort {

    /**
     * Creates or retrieves a GitHub issue URL based on the defect ID.
     *
     * @param defectId The unique identifier of the defect (e.g., "VW-454").
     * @return An Optional containing the URL string if present, empty otherwise.
     */
    Optional<String> getIssueUrl(String defectId);
}
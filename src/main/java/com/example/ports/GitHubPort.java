package com.example.ports;

import java.util.Optional;

public interface GitHubPort {
    /**
     * Create a GitHub issue for a defect report
     * @param defectId The defect ID
     * @param title The issue title
     * @return The URL of the created issue
     */
    String createIssue(String defectId, String title);
    
    /**
     * Get the URL for an existing issue
     * @param defectId The defect ID
     * @return Optional containing the URL if exists
     */
    Optional<String> getIssueUrl(String defectId);
    
    /**
     * Setup mock data for testing
     * @param defectId The defect ID
     * @param title The issue title
     */
    void setupIssueCreation(String defectId, String title);
}
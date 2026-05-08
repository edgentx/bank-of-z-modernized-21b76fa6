package com.example.ports;

/**
 * Port interface for GitHub Issue operations.
 * Used to retrieve URL information for defects.
 */
public interface GitHubIssuePort {

    /**
     * Retrieves the specific URL for a defect report.
     * 
     * @param defectId The unique identifier of the defect.
     * @return The fully qualified HTTP URL to the GitHub issue.
     */
    String getIssueUrl(String defectId);
}

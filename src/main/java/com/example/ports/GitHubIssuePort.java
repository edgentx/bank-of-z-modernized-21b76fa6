package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to create a repository link for defects.
 */
public interface GitHubIssuePort {
    /**
     * Generates the URL for a specific defect ID or creates a remote link.
     * @param defectId The internal ID of the defect.
     * @return The URL string pointing to the GitHub issue.
     */
    String getIssueUrl(String defectId);
}

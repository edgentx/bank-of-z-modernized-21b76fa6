package com.example.domain.validation.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue for the given defect ID.
     * @param defectId The unique identifier for the defect.
     * @return The URL of the created issue.
     */
    String createIssue(String defectId);
}

package com.example.ports;

/**
 * Interface for GitHub Issue tracking operations.
 * Used by the Validation workflow to externalize defect tracking.
 */
public interface GitHubPort {

    /**
     * Creates a new GitHub issue based on the defect report.
     * @param defectId The internal defect ID (e.g., VW-454)
     * @param title The title of the defect
     * @param body The description body
     * @return The fully qualified URL to the created GitHub issue.
     */
    String createIssue(String defectId, String title, String body);
}

package com.example.ports;

/**
 * Port interface for external VForce360/Temporal notifications.
 * Abstracts Slack and GitHub API interactions.
 */
public interface VForce360NotificationPort {

    /**
     * Notifies the Slack channel and creates a GitHub issue for the defect.
     *
     * @param defectId The ID of the defect.
     * @param title The title of the defect.
     * @param description The description/body.
     * @return The URL of the created GitHub issue.
     */
    String reportDefectAndCreateIssue(String defectId, String title, String description);
}

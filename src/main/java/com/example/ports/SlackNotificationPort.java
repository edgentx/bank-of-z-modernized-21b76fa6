package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by Temporal workflows and domain services.
 */
public interface SlackNotificationPort {

    /**
     * Sends a defect report notification to Slack.
     *
     * @param projectId The ID of the project (e.g., 21b76fa6-...)
     * @param defectId The ID of the defect (e.g., VW-454)
     * @param summary The summary of the defect
     * @param description The full description of the defect (including GitHub links)
     */
    void sendDefectReport(String projectId, String defectId, String summary, String description);
}
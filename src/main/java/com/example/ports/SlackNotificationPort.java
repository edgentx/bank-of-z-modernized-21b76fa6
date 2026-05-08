package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the temporal worker workflow or domain handlers to alert the team.
 */
public interface SlackNotificationPort {

    /**
     * Formats and sends a defect notification to Slack.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @return The formatted message body that was sent (useful for testing/verification).
     */
    String formatDefectNotification(String defectId);

    /**
     * Sends the notification.
     */
    void sendNotification(String messageBody);
}

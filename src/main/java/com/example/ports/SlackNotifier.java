package com.example.ports;

/**
 * Port interface for Slack notifications.
 * This allows mocking in tests and switching implementations.
 */
public interface SlackNotifier {
    
    /**
     * Formats the defect report body.
     * This isolates the logic for string construction so it can be tested easily.
     */
    String formatDefectBody(String defectId, String description, String severity, String projectId);

    /**
     * Sends the notification (actual side effect).
     */
    void sendNotification(String channel, String body);
}

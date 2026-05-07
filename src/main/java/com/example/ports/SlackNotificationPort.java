package com.example.ports;

/** Port for sending Slack notifications. */
public interface SlackNotificationPort {
    /**
     * Sends a defect report to Slack.
     * @param projectId The ID of the project.
     * @param message The message body to send.
     */
    void sendDefectNotification(String projectId, String message);
}

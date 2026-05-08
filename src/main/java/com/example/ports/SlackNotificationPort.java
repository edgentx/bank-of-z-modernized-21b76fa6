package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal workflow to communicate with the outside world.
 */
public interface SlackNotificationPort {
    /**
     * Sends a defect report to Slack.
     * @param message The formatted message body.
     */
    void sendDefectReport(String message);
}

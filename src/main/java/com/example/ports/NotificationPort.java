package com.example.ports;

/**
 * Port for sending notifications (e.g., to Slack).
 * Used by the Validation workflow to report defects.
 */
public interface NotificationPort {

    /**
     * Sends a formatted notification message.
     *
     * @param body The content of the message.
     */
    void sendNotification(String body);
}

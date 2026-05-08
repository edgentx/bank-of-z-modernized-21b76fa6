package com.example.ports;

/**
 * Port for sending notifications to external systems (e.g., Slack).
 */
public interface NotificationPort {
    /**
     * Sends a notification message.
     * @param message The message content to send
     */
    void sendNotification(String message);
}

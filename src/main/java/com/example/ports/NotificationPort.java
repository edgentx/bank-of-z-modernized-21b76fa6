package com.example.ports;

/**
 * Port interface for sending notifications (e.g., to Slack).
 * This decouples the domain logic from the specific notification implementation.
 */
public interface NotificationPort {

    /**
     * Sends a notification message.
     *
     * @param recipient The identifier for the recipient (e.g., channel ID).
     * @param subject   The subject of the notification.
     * @param body      The body content of the notification.
     * @return true if sending was successful, false otherwise.
     */
    boolean sendNotification(String recipient, String subject, String body);
}

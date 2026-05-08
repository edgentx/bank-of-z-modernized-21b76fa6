package com.example.ports;

/**
 * Port for sending notifications (e.g., Slack, Email).
 * Used by the temporal workflow logic to decouple from specific implementations.
 */
public interface NotificationPort {
    
    /**
     * Sends a notification with a subject and a body.
     * @param subject The subject line.
     * @param body The body content, expected to contain the GitHub URL if applicable.
     */
    void sendNotification(String subject, String body);
}

package com.example.domain.validation.ports;

/**
 * Port for sending notifications (e.g., to Slack).
 * This isolates the domain logic from external HTTP clients.
 */
public interface NotificationPort {
    /**
     * Sends a notification payload.
     * @param payload The formatted message body.
     */
    void sendNotification(String payload);
}

package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implementations will likely use the Slack SDK or a generic WebClient.
 * This decouples the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}

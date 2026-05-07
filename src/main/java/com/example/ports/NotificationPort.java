package com.example.ports;

/**
 * Port for sending notifications to external systems like Slack.
 * This isolates the domain logic from the HTTP client implementation details.
 */
public interface NotificationPort {
    void sendNotification(String targetChannel, String messageBody);
}

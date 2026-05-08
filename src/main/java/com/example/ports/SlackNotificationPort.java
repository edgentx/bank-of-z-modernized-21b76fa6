package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implementations (real adapters) will talk to Slack API.
 * Tests will use a Mock implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     * @param messageBody The formatted message body.
     */
    void sendNotification(String messageBody);
}

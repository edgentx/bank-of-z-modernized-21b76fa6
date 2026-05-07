package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This isolates the domain logic from the specific Slack client library implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     * @param messageBody The formatted message body to send.
     */
    void send(String messageBody);
}

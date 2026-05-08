package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain to decouple from the specific Slack implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     * @param body The formatted message body to send.
     */
    void send(String body);
}

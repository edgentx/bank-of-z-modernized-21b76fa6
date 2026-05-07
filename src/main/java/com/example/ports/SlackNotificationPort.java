package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     * @param messageBody The formatted message to send.
     */
    void notify(String messageBody);
}

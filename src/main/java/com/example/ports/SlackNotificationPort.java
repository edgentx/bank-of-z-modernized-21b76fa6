package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstraction used to allow mocking in tests and real implementation in production.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param messageBody The formatted message body
     */
    void sendMessage(String channel, String messageBody);
}

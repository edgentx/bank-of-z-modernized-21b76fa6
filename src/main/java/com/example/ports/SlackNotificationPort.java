package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstraction used to decouple domain logic from specific Slack client implementations.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     */
    void sendMessage(String channel, String messageBody);
}

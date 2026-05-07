package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal worker logic to decouple from the concrete Slack SDK.
 */
public interface SlackNotifierPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if channel or body is invalid.
     */
    void send(String channel, String messageBody);
}

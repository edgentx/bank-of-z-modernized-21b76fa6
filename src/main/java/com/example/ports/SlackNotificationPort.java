package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the application layer to decouple from specific Slack implementations.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if the message body is null or invalid.
     */
    void send(String messageBody);
}

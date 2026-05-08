package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the domain logic to decouple from the actual Slack API implementation.
 */
public interface SlackNotifier {
    /**
     * Sends a notification message to the configured Slack channel.
     *
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if messageBody is null or empty.
     */
    void notify(String messageBody);
}

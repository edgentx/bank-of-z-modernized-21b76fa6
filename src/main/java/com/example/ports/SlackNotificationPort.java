package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstraction used to allow mocking in tests without real I/O.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The body content of the message.
     * @throws IllegalArgumentException if channel or body is invalid.
     * @throws RuntimeException if the external API call fails.
     */
    void sendMessage(String channel, String messageBody);

    /**
     * Retrieves the last message sent to the specified channel.
     * Used primarily in test mocks to verify behavior.
     *
     * @param channel The channel identifier.
     * @return The last message body, or null if no message has been sent.
     */
    String getLastMessage(String channel);
}
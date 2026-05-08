package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message body to a specific Slack channel.
     *
     * @param channel The target channel.
     * @param body    The content of the message.
     */
    void sendMessage(String channel, String body);
    
    /**
     * Helper to retrieve the last message sent to a specific channel for verification.
     * Note: This is typically implemented by mocks, but kept in the interface
     * to allow stateful in-memory verification if needed without casting.
     */
    default String getLastMessageBody(String channel) {
        throw new UnsupportedOperationException("Not implemented in production adapter.");
    }
}
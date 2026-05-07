package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Real implementation would post to Slack API.
 * Test implementation captures state for assertions.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to the configured Slack channel.
     * @param messageBody The formatted text to send.
     */
    void sendMessage(String messageBody);

    /**
     * Retrieves the body of the last message sent.
     * Used for verification in tests.
     * @return The message body string.
     */
    String getLastMessageBody();
}

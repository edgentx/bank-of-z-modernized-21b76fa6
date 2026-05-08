package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal worker logic to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body content
     */
    void sendMessage(String channel, String body);

    /**
     * Helper to capture the last message sent during tests/mocking.
     * In a real implementation, this might not be here, but for our mock pattern
     * we often verify interactions directly via Mockito.
     */
}

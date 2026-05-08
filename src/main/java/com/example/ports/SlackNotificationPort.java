package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Implemented by the real Slack adapter and mocked in tests.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message
     * @return true if sending was acknowledged, false otherwise
     */
    boolean sendMessage(String channel, String messageBody);

    /**
     * Retrieves the last message sent to the specific channel.
     * Used by temporal workflows to verify state.
     *
     * @param channel The target channel
     * @return The body of the last message
     */
    String getLastMessageBody(String channel);
}
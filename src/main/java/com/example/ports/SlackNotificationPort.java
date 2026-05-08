package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify that the correct content (specifically GitHub URLs) is sent.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a Slack channel.
     * @param channelId The target channel.
     * @param messageBody The content of the message.
     */
    void sendMessage(String channelId, String messageBody);

    /**
     * Retrieves the last message body sent to a specific channel.
     * Used primarily for testing/verification to satisfy the defect requirements.
     * @param channelId The target channel.
     * @return The last message body string, or null if no message exists.
     */
    String getLastMessageBody(String channelId);
}
package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to validate message content before or during delivery.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The target channel (e.g., "C12345")
     * @param messageBody The content of the message (formatted Slack markup)
     * @return true if sending was successful, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}

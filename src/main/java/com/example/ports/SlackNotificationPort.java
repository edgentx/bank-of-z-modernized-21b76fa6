package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification payload to a Slack channel.
     *
     * @param channel The Slack channel ID or name.
     * @param messageBody The formatted message body to send.
     * @throws IllegalArgumentException if messageBody is null/blank.
     */
    void sendNotification(String channel, String messageBody);

    /**
     * Returns the last message body sent to the specific channel.
     * Helper method for testing state.
     */
    String getLastMessageBody(String channel);
}
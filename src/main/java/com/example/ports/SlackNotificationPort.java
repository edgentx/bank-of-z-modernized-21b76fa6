package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used in the defect reporting workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a configured Slack channel.
     * 
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message.
     */
    void sendMessage(String channel, String messageBody);

    /**
     * Retrieves the last message body sent to a specific channel.
     * (Used in test verification and diagnostic checks).
     * 
     * @param channel The target channel.
     * @return The last message body string, or null if none exists.
     */
    String getLastMessageBody(String channel);
}

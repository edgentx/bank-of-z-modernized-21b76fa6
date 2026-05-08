package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by domain services to decouple from the actual Slack API client.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message
     * @return true if the message was accepted by the client, false otherwise
     */
    boolean sendMessage(String channel, String messageBody);
}

package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstraction for the actual Slack WebClient.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a specific channel.
     * 
     * @param channel The target channel (e.g., #vforce360-issues).
     * @param messageBody The formatted body content.
     */
    void sendMessage(String channel, String messageBody);
}

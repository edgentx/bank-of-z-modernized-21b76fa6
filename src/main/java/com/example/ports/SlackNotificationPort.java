package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the temporal worker to notify channels.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a formatted message to a configured Slack channel.
     * 
     * @param messageBody The full text body of the message.
     */
    void sendNotification(String messageBody);
}

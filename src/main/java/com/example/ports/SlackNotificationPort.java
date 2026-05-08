package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification to the configured Slack channel.
     * 
     * @param message The body of the message to send.
     * @throws IllegalArgumentException if the message is invalid or missing required fields.
     * @return true if the message was accepted by the mock/adapter.
     */
    boolean sendNotification(String message);
}
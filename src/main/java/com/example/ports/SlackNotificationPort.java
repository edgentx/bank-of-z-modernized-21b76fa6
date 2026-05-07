package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the Slack API client from the domain logic.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a configured Slack channel.
     *
     * @param message The body of the message to send.
     * @throws IllegalArgumentException if the message is null or empty.
     */
    void sendMessage(String message);
}

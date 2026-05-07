package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to decouple the application logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param message The body of the message to send.
     * @throws IllegalArgumentException if the message is null or empty.
     */
    void sendNotification(String message);
}

package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param message The content of the message to send.
     * @throws IllegalArgumentException if the message body is null or blank.
     */
    void sendNotification(String message);
}

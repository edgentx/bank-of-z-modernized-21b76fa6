package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the Temporal workflow logic from the actual Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The formatted message to be sent.
     * @throws RuntimeException if the notification fails to send.
     */
    void sendNotification(String payload);
}
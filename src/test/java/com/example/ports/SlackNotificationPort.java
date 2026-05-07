package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a configured Slack channel.
     *
     * @param payload The formatted message body to be sent to Slack.
     * @return true if the notification was accepted by the client, false otherwise.
     */
    boolean sendNotification(String payload);
}

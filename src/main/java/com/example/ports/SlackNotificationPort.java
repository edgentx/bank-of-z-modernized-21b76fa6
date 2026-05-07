package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstracts the external Slack API interaction.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The formatted JSON string or structured object to send.
     * @throws Exception if the notification fails.
     */
    void send(String payload) throws Exception;
}

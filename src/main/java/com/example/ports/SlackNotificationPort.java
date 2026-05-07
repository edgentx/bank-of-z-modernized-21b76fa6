package com.example.ports;

/**
 * Port for Slack notifications.
 * Implementation should handle formatting details and API interaction.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to the configured Slack channel.
     *
     * @param message The formatted message body to send.
     */
    void postMessage(String message);
}
package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific channel.
     *
     * @param channelId The ID of the Slack channel (e.g., "C0123456789").
     * @param message   The formatted message body to send.
     */
    void postMessage(String channelId, String message);
}

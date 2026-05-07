package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a specific channel.
     *
     * @param channelId The target channel ID.
     * @param message   The message content.
     */
    void postMessage(String channelId, String message);
}

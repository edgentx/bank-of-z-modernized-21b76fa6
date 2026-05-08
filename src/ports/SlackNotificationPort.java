package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implemented by adapters to interact with real Slack API or mocks.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a channel.
     *
     * @param channelId The target channel ID.
     * @param text      The message body.
     */
    void postMessage(String channelId, String text);
}
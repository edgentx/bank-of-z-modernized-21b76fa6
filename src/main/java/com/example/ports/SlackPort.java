package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstracts the underlying Slack API client implementation.
 */
public interface SlackPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g., "C0123456789").
     * @param text The message body text.
     * @return true if the post was successful, false otherwise.
     */
    boolean postMessage(String channelId, String text);
}
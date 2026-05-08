package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackPort {
    /**
     * Posts a message to a specific channel.
     * @param channelId The target channel ID
     * @param text The message body/content
     */
    void postMessage(String channelId, String text);
}

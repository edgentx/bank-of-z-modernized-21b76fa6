package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Allows switching between real implementation and mocks.
 */
public interface SlackPort {

    /**
     * Posts a message to a specific channel.
     *
     * @param channelId The target channel ID (e.g., "C0123456789").
     * @param text      The message body text.
     * @return true if successful, false otherwise.
     */
    boolean postMessage(String channelId, String text);
}

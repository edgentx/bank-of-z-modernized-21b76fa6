package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the application to send messages without depending directly on the implementation.
 */
public interface SlackPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345").
     * @param message The formatted message body.
     * @throws RuntimeException if the API call fails.
     */
    void sendMessage(String channelId, String message);
}

package com.example.ports;

/**
 * Port for sending notifications (e.g., to Slack).
 */
public interface NotificationPort {
    /**
     * Sends a notification to a specific channel.
     * @param channel The channel ID or name.
     * @param message The message content.
     */
    void send(String channel, String message);
}

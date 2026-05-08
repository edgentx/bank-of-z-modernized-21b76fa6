package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    /**
     * Sends a message to a specific channel.
     * @param channel The channel ID or name
     * @param message The message content
     */
    void sendMessage(String channel, String message);
}

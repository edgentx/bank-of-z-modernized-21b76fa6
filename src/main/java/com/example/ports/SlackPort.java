package com.example.ports;

/**
 * Port for posting Slack messages.
 */
public interface SlackPort {
    /**
     * Sends a message to a specific channel.
     * @param channel The channel ID or name
     * @param text The message text
     */
    void sendMessage(String channel, String text);
}

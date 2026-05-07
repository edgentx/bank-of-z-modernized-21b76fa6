package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The channel ID or name.
     * @param text    The message content.
     */
    void sendMessage(String channel, String text);
}

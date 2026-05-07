package com.example.ports;

/**
 * Port for Slack notification operations.
 * Used to decouple the core logic from the actual Slack WebClient.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a channel.
     * @param channel The channel ID or name.
     * @param body The formatted body of the message.
     */
    void postMessage(String channel, String body);
}

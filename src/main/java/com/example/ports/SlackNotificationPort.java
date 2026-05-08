package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal workflow logic.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345")
     * @param message   The body of the message
     * @throws IllegalArgumentException if the message is invalid
     */
    void postMessage(String channelId, String message);
}

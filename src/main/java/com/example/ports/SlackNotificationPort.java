package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID.
     * @param messageBody The formatted message body.
     * @throws IllegalArgumentException if the message body is invalid.
     */
    void postMessage(String channelId, String messageBody);
}

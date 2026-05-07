package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the core logic from the actual Slack WebClient implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g., "C0123456789").
     * @param messageBody The content of the message (formatted text).
     * @return true if the API call was accepted, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}
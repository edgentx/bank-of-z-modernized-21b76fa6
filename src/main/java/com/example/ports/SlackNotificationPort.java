package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the core logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The ID of the Slack channel (e.g., "C0123456789").
     * @param messageBody The content of the message to be sent.
     * @return true if the message was accepted by the port successfully, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}
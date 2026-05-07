package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a specific channel.
     *
     * @param channelId The ID of the Slack channel (e.g., "C0123456789").
     * @param messageBody The formatted content of the message.
     * @return true if the message was successfully enqueued/sent, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}
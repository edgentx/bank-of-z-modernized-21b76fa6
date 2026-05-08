package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a specific Slack channel.
     *
     * @param channel The name of the channel (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     */
    void sendMessage(String channel, String messageBody);
}

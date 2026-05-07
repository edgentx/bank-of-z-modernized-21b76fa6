package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This decouples the domain logic from the specific Slack Web API client implementation.
 */
public interface SlackPort {

    /**
     * Sends a notification message to a specific channel.
     *
     * @param channel      The channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody  The formatted text or JSON payload for the message.
     */
    void sendNotification(String channel, String messageBody);
}

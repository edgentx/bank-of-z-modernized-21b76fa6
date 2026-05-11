package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues").
     * @param body    The text content of the message.
     */
    void send(String channel, String body);
}

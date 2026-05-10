package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the defect reporting workflow to publish messages.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param body    The formatted message body.
     * @throws IllegalArgumentException if channel or body is invalid.
     */
    void send(String channel, String body);
}
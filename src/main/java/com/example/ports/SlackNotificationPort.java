package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows or domain services to alert users.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to a Slack channel.
     *
     * @param channelId The target channel (e.g., "#vforce360-issues").
     * @param body      The message body content.
     */
    void send(String channelId, String body);
}

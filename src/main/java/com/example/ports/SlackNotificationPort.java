package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body    The message body content.
     */
    void postMessage(String channel, String body);
}

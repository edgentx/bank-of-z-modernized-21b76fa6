package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow to update channels.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message body to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g. "#vforce360-issues").
     * @param body    The formatted message body to send.
     */
    void sendMessage(String channel, String body);
}

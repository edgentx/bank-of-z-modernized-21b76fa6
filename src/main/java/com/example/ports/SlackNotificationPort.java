package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Temporal worker to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a channel.
     * @param channel The Slack channel ID or name (e.g. #vforce360-issues).
     * @param body The message body content.
     */
    void sendMessage(String channel, String body);
}
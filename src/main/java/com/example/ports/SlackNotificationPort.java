package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Implementations should handle the specifics of the Slack API.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues").
     * @param body    The message body content.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean send(String channel, String body);
}

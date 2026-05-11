package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to report defects or status updates.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param body    The formatted body of the message (supports Slack markup).
     * @return true if the API accepted the request, false otherwise.
     */
    boolean sendMessage(String channel, String body);
}

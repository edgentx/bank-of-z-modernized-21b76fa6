package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by defect reporting workflows.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues")
     * @param message The message body content
     * @throws com.example.domain.shared.UnknownCommandException if the post fails
     */
    void postMessage(String channel, String message);
}

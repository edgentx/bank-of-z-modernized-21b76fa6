package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Temporal workflow to alert users.
 */
public interface SlackPort {
    /**
     * Posts a message to a specific Slack channel.
     * @param channel The channel ID or name (e.g. #vforce360-issues).
     * @param body The formatted message body.
     */
    void postMessage(String channel, String body);
}

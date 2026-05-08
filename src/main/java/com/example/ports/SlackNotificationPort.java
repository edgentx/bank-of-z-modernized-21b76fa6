package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by workflows to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues").
     * @param body    The formatted message body.
     */
    void postMessage(String channel, String body);
}

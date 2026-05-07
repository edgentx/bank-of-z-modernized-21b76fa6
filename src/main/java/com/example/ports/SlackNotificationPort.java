package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows/activities.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body text.
     */
    void postMessage(String channel, String body);
}

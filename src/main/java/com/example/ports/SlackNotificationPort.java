package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Temporal workflow to communicate results.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The message body content
     */
    void send(String channel, String body);
}

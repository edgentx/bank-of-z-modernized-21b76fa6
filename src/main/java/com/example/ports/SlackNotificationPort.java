package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the validation service to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g. "#vforce360-issues").
     * @param body The message body content.
     */
    void send(String channel, String body);
}
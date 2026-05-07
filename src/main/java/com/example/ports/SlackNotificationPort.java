package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by defect reporting workflows to broadcast results.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body text
     */
    void sendMessage(String channel, String body);
}

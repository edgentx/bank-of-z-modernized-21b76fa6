package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to report defects.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted message body.
     * @throws IllegalArgumentException if channel or body is invalid.
     */
    void postMessage(String channel, String messageBody);
}
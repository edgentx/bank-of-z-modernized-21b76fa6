package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted content of the message
     * @throws IllegalArgumentException if channel or body is invalid
     */
    void postMessage(String channel, String messageBody);
}

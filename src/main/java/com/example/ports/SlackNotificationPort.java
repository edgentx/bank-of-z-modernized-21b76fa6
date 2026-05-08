package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by defect reporting workflows.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The formatted message body
     */
    void send(String channel, String messageBody);
}
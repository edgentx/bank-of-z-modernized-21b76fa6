package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal workflow to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted content to send.
     * @throws IllegalArgumentException if the message body is invalid.
     */
    void send(String messageBody);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This isolates the core logic from the Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to send.
     */
    void send(String messageBody);
}
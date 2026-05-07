package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by temporal workflows to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param message The formatted message body to send.
     */
    void send(String message);
}
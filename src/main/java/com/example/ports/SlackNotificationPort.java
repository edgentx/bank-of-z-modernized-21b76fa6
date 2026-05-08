package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to validate that Slack bodies contain required URLs (e.g., GitHub issue links).
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to the configured Slack channel.
     * @param body The message body content.
     * @throws IllegalArgumentException if the body is invalid or missing required fields.
     */
    void postMessage(String body);
}
package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to Slack.
     * @param message The main message text.
     * @param details Supplementary details (e.g., a GitHub URL).
     */
    void postMessage(String message, String details);
}
package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to alert on defect reports.
 */
public interface SlackPort {
    /**
     * Sends a message to the configured Slack channel.
     *
     * @param message The formatted message body.
     */
    void sendMessage(String message);
}
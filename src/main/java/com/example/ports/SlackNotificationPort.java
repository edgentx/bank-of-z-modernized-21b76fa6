package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to validate the presence of GitHub URLs in defect reports.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The formatted message body.
     * @throws IllegalArgumentException if the payload is invalid (e.g., missing required URL).
     */
    void send(String payload);
}

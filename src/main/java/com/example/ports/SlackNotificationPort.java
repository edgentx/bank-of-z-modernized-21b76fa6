package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Temporal workflow implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to the configured Slack channel.
     *
     * @param payload The formatted message to be sent.
     * @return true if the API call accepted the request, false otherwise.
     */
    boolean send(String payload);
}
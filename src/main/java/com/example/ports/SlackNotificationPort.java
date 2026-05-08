package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstraction used to allow mocking in tests without real I/O.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The JSON payload to send.
     * @return true if sending was successful, false otherwise.
     */
    boolean send(String payload);
}
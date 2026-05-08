package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implementations will interact with the real Slack API, but tests will use mocks.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message payload to Slack.
     *
     * @param payload The formatted JSON payload or string to send.
     */
    void send(String payload);

    /**
     * Retrieves the last payload sent to Slack. Useful for verification in tests.
     *
     * @return The last sent payload string.
     */
    String getLastPayload();
}

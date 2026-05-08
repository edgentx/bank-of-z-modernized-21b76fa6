package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to verify defect VW-454.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     * @param payload The formatted message body.
     */
    void send(String payload);

    /**
     * Returns the last payload sent to Slack (for test verification).
     */
    String getLastSentPayload();
}
package com.example.ports;

/**
 * Port for Slack notifications.
 */
public interface SlackPort {
    /**
     * Sends a notification to Slack.
     * @param messageBody The formatted JSON payload.
     */
    void sendNotification(String messageBody);
}

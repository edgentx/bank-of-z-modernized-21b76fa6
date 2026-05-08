package com.example.ports;

/**
 * Port for sending notifications to external systems like Slack.
 * This interface must be implemented by the production adapter and mocked in tests.
 */
public interface NotificationGatewayPort {

    /**
     * Sends a notification message.
     *
     * @param channelId The target channel ID (e.g. Slack channel)
     * @param messageBody The formatted message body
     */
    void sendNotification(String channelId, String messageBody);
}

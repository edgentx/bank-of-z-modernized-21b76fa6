package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the VForce360 diagnostic workflow.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to send.
     */
    void sendNotification(String messageBody);
}

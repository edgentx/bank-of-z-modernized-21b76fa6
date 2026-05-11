package com.example.ports;

/**
 * Port for sending Slack notifications.
 * This allows the domain logic to decouple from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to be sent.
     */
    void sendNotification(String messageBody);
}
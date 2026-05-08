package com.example.adapters;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to the configured Slack channel.
     * @param messageBody The formatted message body.
     */
    void sendNotification(String messageBody);
}

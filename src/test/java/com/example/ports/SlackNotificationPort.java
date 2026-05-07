package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification message to Slack.
     */
    void sendNotification(String messageBody);
}

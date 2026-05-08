package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the domain to decouple from specific Slack implementation.
 */
public interface SlackNotificationPort {
    void sendNotification(String message);
}

package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Implementations must handle the HTTP interaction.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String messageBody);
}

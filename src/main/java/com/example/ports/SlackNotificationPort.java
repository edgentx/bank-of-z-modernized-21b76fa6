package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by domain services or event listeners.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String messageBody);
}
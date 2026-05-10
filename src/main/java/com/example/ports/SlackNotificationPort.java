package com.example.ports;

/**
 * Port interface for Slack Notification.
 * Used by the domain to send notifications without depending on concrete implementations.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}

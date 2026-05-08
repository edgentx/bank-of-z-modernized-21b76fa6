package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implementations will likely use the Slack SDK or a generic WebClient.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}
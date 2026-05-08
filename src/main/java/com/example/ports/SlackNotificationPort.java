package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Acts as the boundary for the external Slack API.
 */
public interface SlackNotificationPort {
    void sendNotification(String message);
}
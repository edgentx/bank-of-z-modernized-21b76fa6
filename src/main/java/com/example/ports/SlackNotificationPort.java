package com.example.ports;

/**
 * Port interface for Slack Notifications.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String body);
}

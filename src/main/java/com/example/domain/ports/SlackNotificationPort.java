package com.example.domain.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}

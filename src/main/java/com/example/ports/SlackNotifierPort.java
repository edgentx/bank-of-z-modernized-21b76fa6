package com.example.ports;

/**
 * Port interface for Slack notifications.
 */
public interface SlackNotifierPort {
    void sendNotification(String channel, String body);
}

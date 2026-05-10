package com.example.ports;

/**
 * Port for Slack notifications.
 */
public interface SlackNotificationPort {
    void notify(String channel, String message);
}

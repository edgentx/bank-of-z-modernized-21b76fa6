package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by Temporal workflows to alert users.
 */
public interface SlackNotificationPort {
    void send(String messageBody);
}

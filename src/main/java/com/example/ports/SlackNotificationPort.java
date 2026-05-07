package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by Temporal workflows to report defects.
 */
public interface SlackNotificationPort {
    void sendMessage(String messageBody);
}

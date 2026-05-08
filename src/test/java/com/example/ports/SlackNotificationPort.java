package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Allows mocking in unit tests and real implementation in production.
 */
public interface SlackNotificationPort {
    void sendMessage(String body);
}

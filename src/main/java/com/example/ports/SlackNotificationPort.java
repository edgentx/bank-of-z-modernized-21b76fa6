package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Implemented by adapters (e.g., actual Slack HTTP client, or Mock for testing).
 */
public interface SlackNotificationPort {
    void sendMessage(String body);
}

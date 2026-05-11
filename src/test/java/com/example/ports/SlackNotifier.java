package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Implemented by adapters in src/main/java and mocked in tests.
 */
public interface SlackNotifier {
    void sendNotification(String message);
}

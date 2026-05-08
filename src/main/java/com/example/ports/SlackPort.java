package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Allows decoupling business logic from specific Slack client implementations.
 */
public interface SlackPort {
    void sendNotification(String title, String body);
}

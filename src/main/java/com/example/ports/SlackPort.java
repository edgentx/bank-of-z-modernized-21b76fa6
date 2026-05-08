package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Decouples the application logic from the specific Slack implementation.
 */
public interface SlackPort {
    void sendNotification(String message);
}
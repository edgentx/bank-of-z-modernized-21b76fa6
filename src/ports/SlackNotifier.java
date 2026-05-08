package com.example.ports;

/**
 * Port interface for Slack notifications.
 */
public interface SlackNotifier {
    void sendNotification(String message);
}

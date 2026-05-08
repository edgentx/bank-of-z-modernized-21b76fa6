package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Adapters must implement this to interact with the actual Slack Webhook API.
 */
public interface SlackNotificationPort {
    void notify(String message);
}
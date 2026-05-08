package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Implemented by adapters responsible for HTTP calls to Slack Web API.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}

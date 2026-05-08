package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}

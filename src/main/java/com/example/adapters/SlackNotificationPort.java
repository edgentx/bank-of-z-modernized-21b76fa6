package com.example.adapters;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain to decouple from the specific Slack client implementation.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String body);
}
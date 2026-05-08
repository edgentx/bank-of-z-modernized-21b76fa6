package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstracts the external Slack API interaction.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String messageBody);
}
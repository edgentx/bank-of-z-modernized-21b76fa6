package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used in Story S-FB-1 to verify defect reporting output.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}

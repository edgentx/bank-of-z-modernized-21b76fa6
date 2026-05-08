package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}
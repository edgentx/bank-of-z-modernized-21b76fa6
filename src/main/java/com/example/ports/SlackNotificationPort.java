package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotificationPort {
    void notify(String messageBody);
}
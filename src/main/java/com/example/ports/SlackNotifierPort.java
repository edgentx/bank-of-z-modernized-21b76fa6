package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotifierPort {
    void sendNotification(String message);
}
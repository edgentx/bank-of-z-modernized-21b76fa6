package com.example.ports;

/**
 * Port for Slack notification operations.
 */
public interface SlackNotificationPort {

    void sendNotification(String channel, String body);
}
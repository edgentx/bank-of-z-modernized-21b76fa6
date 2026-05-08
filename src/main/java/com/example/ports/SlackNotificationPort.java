package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal-worker logic.
 */
public interface SlackNotificationPort {
    void send(String body);
}
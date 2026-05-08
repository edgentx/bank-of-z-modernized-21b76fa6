package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the domain to verify integration without hard dependencies.
 */
public interface SlackNotificationPort {
    void send(String messageBody);
}

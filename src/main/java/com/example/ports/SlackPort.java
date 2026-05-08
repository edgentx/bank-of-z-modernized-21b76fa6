package com.example.ports;

/**
 * Interface for Slack notifications.
 * Used by Temporal workers to send alerts.
 */
public interface SlackPort {
    void sendMessage(String messageBody);
}

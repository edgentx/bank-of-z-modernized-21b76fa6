package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the domain logic to send alerts without depending on a concrete implementation.
 */
public interface SlackNotifierPort {
    void sendNotification(String messageBody);
}

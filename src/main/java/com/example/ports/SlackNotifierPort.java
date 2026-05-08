package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotifierPort {
    void send(String messageBody);
}

package com.example.domain.validation.port;

/**
 * Port interface for Slack notifications.
 * Used by the domain logic to send alerts without depending on concrete implementations.
 */
public interface SlackNotificationPort {
    void send(String body);
}

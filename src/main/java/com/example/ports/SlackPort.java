package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the application to send messages without depending on the concrete implementation.
 */
public interface SlackPort {
    void sendMessage(String message);
}

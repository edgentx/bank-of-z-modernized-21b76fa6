package com.example.ports;

/**
 * Port interface for Slack notifications.
 */
public interface SlackPort {
    void sendMessage(String body);
}

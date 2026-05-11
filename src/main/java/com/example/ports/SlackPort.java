package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Implementations will handle HTTP calls to Slack Webhooks.
 */
public interface SlackPort {
    void sendMessage(String body);
}

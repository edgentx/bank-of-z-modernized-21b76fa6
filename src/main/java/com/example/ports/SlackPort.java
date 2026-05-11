package com.example.ports;

/**
 * Port interface for Slack notifications.
 * This decouples the domain logic from the specific Slack API implementation.
 */
public interface SlackPort {
    void postMessage(String channel, String text);
}

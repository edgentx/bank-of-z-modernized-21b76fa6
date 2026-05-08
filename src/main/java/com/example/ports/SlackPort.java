package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Abstraction allows mocking in tests and swapping implementations in production.
 */
public interface SlackPort {
    void postMessage(String channel, String body);
}

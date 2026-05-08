package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Implemented by adapters (e.g., real HTTP client or test mock).
 */
public interface SlackNotifierPort {
    void notify(String body);
}

package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by tests to mock Slack interaction.
 */
public interface SlackPort {
    void sendMessage(String channel, String body);
}

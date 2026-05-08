package com.example.ports;

/**
 * Port for Slack notifications.
 */
public interface SlackPort {
    void sendMessage(String channel, String message);
}

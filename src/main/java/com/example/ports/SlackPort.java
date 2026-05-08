package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackPort {
    void sendMessage(String channel, String text);
}

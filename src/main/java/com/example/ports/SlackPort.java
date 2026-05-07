package com.example.ports;

/**
 * Port for Slack notification operations.
 */
public interface SlackPort {
    void sendMessage(String message);
}

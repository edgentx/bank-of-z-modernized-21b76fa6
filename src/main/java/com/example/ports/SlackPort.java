package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * This decouples the domain logic from the specific Slack SDK implementation.
 */
public interface SlackPort {
    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., #vforce360-issues).
     * @param body    The formatted message body.
     */
    void sendMessage(String channel, String body);
}
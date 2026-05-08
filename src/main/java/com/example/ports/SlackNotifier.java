package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackNotifier {
    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues")
     * @param message The message body
     */
    void send(String channel, String message);
}

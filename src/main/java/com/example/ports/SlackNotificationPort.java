package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This decouples the application logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param message The body of the message.
     * @throws IllegalArgumentException if the message is null or empty.
     */
    void send(String channel, String message);

    /**
     * Helper to verify the connection/scope (optional).
     */
    default void healthCheck() {
        // Default no-op
    }
}
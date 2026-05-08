package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotifier {
    /**
     * Sends a notification to the configured Slack channel.
     * @param message The formatted message to send.
     */
    void send(String message);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    /**
     * Sends a notification message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param message The message body to send
     */
    void sendMessage(String channel, String message);
}
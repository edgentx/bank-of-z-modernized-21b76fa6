package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    /**
     * Sends a message to a specific Slack channel.
     * @param channel The channel ID or name (e.g., #vforce360-issues)
     * @param messageBody The formatted message body to send
     */
    void sendMessage(String channel, String messageBody);
}
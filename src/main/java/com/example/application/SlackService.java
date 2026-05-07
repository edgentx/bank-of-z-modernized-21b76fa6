package com.example.application;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackService {
    /**
     * Sends a notification to the configured channel.
     * @param message The message body (expecting Slack markdown).
     */
    void sendMessage(String message);
}
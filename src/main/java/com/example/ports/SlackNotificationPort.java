package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This isolates the core logic from the specific Slack API library.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification to the configured Slack channel.
     * @param messageBody The formatted message to send.
     */
    void sendNotification(String messageBody);
}

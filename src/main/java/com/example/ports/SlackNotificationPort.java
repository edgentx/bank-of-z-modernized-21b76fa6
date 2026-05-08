package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the core logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message. Expected to contain formatted text or blocks.
     */
    void sendNotification(String channel, String messageBody);
}

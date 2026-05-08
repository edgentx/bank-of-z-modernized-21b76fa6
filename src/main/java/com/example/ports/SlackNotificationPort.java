package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain logic to decouple from the specific Slack provider implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a specific channel.
     *
     * @param channel The target Slack channel (e.g., "#vforce360-issues")
     * @param messageBody The formatted body of the message (Slack markdown)
     */
    void sendNotification(String channel, String messageBody);
}

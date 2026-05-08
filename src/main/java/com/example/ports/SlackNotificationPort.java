package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal workflow logic to decouple from the specific implementation.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to send.
     * @return true if sending was acknowledged, false otherwise.
     */
    boolean sendNotification(String messageBody);
}
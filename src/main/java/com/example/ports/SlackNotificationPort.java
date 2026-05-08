package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the domain logic to send messages without depending on the actual Slack SDK.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param messageBody The formatted content to send.
     */
    void sendNotification(String messageBody);
}

package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the validation defect workflow to alert users.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to the configured Slack channel.
     *
     * @param messageBody The formatted content to send.
     */
    void sendNotification(String messageBody);
}
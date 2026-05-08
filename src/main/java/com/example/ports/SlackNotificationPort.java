package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal workflow to alert the engineering team.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to the configured Slack channel.
     *
     * @param messageBody The formatted content of the message.
     */
    void sendNotification(String messageBody);
}

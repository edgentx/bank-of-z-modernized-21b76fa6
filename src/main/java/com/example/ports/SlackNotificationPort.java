package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by Temporal activities/workers to notify channels.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a pre-configured Slack channel.
     *
     * @param messageBody The formatted text to send.
     */
    void sendMessage(String messageBody);
}

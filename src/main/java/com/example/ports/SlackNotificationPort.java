package com.example.ports;

/**
 * Port for sending Slack notifications.
 * This decouples the core logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message
     * @throws RuntimeException if the notification fails
     */
    void postMessage(String channel, String messageBody);
}

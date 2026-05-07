package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack integration implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a specific Slack channel.
     * @param messageBody The formatted message to send.
     */
    void notifyChannel(String messageBody);
}

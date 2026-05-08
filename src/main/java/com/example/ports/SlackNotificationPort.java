package com.example.ports;

/**
 * Interface for Slack notification services.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification message to a specific channel.
     * @param channel The target channel (e.g. #vforce360-issues).
     * @param messageBody The formatted content of the message.
     */
    void sendMessage(String channel, String messageBody);
}
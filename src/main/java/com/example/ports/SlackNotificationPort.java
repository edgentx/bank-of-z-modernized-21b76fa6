package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     */
    void sendMessage(String channel, String messageBody);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Abstracted to allow mocking during testing of the Temporal workflow logic.
 */
public interface SlackNotificationPort {

    /**
     * Sends a formatted message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param messageBody The core content of the message.
     */
    void sendMessage(String channel, String messageBody);
}
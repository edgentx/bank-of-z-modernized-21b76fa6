package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if channel or body is invalid.
     */
    void sendMessage(String channel, String messageBody);

}

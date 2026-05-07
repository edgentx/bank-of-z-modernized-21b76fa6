package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This interface isolates the domain logic from the specific Slack client library.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The ID or name of the channel (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if the message body is invalid (e.g., missing required fields).
     */
    void sendMessage(String channelId, String messageBody);
}

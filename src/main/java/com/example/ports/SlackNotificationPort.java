package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to decouple the core logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body The message body content.
     */
    void sendMessage(String channel, String body);
}

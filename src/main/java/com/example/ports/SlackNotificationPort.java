package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the actual Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a channel with the given body text.
     *
     * @param channel The Slack channel ID or name (e.g. "#vforce360-issues")
     * @param body The message body
     */
    void sendMessage(String channel, String body);
}

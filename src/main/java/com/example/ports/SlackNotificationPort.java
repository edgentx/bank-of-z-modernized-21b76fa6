package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues").
     * @param body    The message body content.
     */
    void sendMessage(String channel, String body);
}

package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This allows us to mock the Slack API in unit tests.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to a Slack channel with a specific text body.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param text    The body text of the message.
     */
    void sendMessage(String channel, String text);
}

package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * This abstraction allows us to mock the Slack API in tests.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific channel.
     *
     * @param channelId The target channel ID or name.
     * @param messageBody The formatted body of the message (Markdown or Slack specific).
     * @throws IllegalArgumentException if channelId or messageBody is null/blank.
     */
    void sendMessage(String channelId, String messageBody);
}
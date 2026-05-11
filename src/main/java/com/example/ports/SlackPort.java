package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Implemented by adapters (e.g., Real Slack API or Mocks for testing).
 */
public interface SlackPort {

    /**
     * Sends a message to a specific channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param text    The message body text.
     * @throws RuntimeException if the API call fails.
     */
    void sendMessage(String channel, String text);
}

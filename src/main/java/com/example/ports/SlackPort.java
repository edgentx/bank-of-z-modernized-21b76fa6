package com.example.ports;

/**
 * Port for posting messages to Slack.
 * Abstracted to allow mocking in tests and verification of message content.
 */
public interface SlackPort {
    
    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The ID of the channel (e.g., "C12345678").
     * @param messageBody The content of the message.
     * @throws IllegalArgumentException if channelId or messageBody is null/blank.
     */
    void postMessage(String channelId, String messageBody);
}

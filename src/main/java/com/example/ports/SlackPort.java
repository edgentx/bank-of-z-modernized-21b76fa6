package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Adapters must implement this to send messages to Slack.
 */
public interface SlackPort {
    /**
     * Publishes a message payload to Slack.
     *
     * @param payload The JSON string payload to send.
     */
    void publish(String payload);
}
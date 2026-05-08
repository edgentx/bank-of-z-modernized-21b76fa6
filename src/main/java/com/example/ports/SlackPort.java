package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Exists to decouple the domain logic from the specific Slack implementation.
 */
public interface SlackPort {

    /**
     * Sends a notification payload to a Slack channel.
     *
     * @param payload The formatted JSON or string message body.
     */
    void sendNotification(String payload);
}

package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * This decouples the domain logic from the specific Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param body    The message body content
     */
    void postMessage(String channel, String body);
}

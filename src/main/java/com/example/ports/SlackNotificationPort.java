package com.example.ports;

import java.util.Map;

/**
 * Port for sending Slack notifications.
 * Defines the contract for the asynchronous messaging adapter.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a Slack channel.
     * @param channel The target channel ID or name
     * @param payload The message content (blocks, text, etc.)
     */
    void publish(String channel, Map<String, Object> payload);
}

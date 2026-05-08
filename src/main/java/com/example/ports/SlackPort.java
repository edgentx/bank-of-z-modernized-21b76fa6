package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 * Used by the domain logic to decouple from the specific Slack SDK implementation.
 */
public interface SlackPort {

    /**
     * Sends a notification to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The text body of the message.
     * @param contextMap Additional metadata (thread_id, user_id, etc.)
     */
    void sendNotification(String channel, String body, Map<String, String> contextMap);
}

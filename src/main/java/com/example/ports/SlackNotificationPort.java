package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 * Used by Temporal workflows/activities to decouple from specific client implementations.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param payload The message body map, expected to contain "text" or other Slack blocks.
     * @throws RuntimeException if the notification fails to send.
     */
    void sendNotification(String channel, Map<String, Object> payload);
}

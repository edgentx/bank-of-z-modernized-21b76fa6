package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 * Implementations must handle the specific formatting of the message body,
 * ensuring that external URLs (like GitHub) are correctly formatted.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messagePayload A map containing the message details.
     *                       Expected keys: "text", "defectId", "githubUrl"
     */
    void sendNotification(Map<String, String> messagePayload);
}
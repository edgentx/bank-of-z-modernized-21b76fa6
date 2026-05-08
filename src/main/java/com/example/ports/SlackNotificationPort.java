package com.example.ports;

import java.util.Map;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain from the actual Slack implementation.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to Slack.
     * @param body The message body.
     * @param attachments Metadata (e.g., GitHub URL, Defect ID).
     */
    void sendNotification(String body, Map<String, String> attachments);
}

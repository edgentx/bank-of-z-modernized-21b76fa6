package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Implementations must handle the actual HTTP transmission to Slack Webhooks.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification to Slack.
     *
     * @param channel The target Slack channel (e.g., #vforce360-issues).
     * @param body    The message body/content.
     */
    void sendNotification(String channel, String body);
}

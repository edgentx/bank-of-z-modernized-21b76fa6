package com.example.adapters;

/**
 * Port interface for sending Slack notifications.
 * Used to alert channels about defect statuses.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     * @param text The message body.
     */
    void postMessage(String text);
}

package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The message body text
     */
    void sendNotification(String channel, String body);
}
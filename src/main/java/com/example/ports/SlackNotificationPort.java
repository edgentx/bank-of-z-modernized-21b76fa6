package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by defect reporting workflows.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body    The formatted message body to send.
     */
    void sendMessage(String channel, String body);
}
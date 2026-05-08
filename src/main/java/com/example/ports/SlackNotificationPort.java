package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal worker logic to report defects.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The formatted message body.
     * @return true if accepted by the mock client, false otherwise.
     */
    boolean sendMessage(String channel, String body);
}
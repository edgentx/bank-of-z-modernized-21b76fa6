package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal-worker defect reporting workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a Slack channel.
     * @param channel The target channel (e.g. "#vforce360-issues").
     * @param body The message body text.
     * @return true if successfully sent, false otherwise.
     */
    boolean sendMessage(String channel, String body);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a specific Slack channel.
     * 
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body content
     */
    void postMessage(String channel, String body);
}

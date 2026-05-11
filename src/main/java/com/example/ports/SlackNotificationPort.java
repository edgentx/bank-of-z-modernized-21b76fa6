package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to report defects.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a Slack channel.
     * 
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body text
     * @return true if sending was successful, false otherwise
     */
    boolean postMessage(String channel, String body);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This is the boundary interface for the Slack integration.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param body The message body content
     * @return true if sending was acknowledged, false otherwise
     */
    boolean sendMessage(String channel, String body);
}

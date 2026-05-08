package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body    The formatted message body.
     * @return true if sending was acknowledged, false otherwise.
     */
    boolean sendMessage(String channel, String body);
}
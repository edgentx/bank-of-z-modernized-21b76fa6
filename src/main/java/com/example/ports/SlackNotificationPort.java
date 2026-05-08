package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the VForce360 diagnostic workflow.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param body    The message body content
     */
    void postMessage(String channel, String body);
}

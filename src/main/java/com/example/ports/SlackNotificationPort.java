package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the VForce360 workflow to report defects.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues").
     * @param body The message body text.
     * @return true if the message was accepted by the API, false otherwise.
     */
    boolean postMessage(String channel, String body);
}

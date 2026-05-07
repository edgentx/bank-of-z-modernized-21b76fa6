package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by VForce360 integration.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to the configured Slack channel.
     *
     * @param payload The formatted JSON string or payload structure.
     * @return true if sending was acknowledged, false otherwise.
     */
    boolean send(String payload);
}

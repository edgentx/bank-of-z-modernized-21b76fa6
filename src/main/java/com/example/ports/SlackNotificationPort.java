package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by Temporal workflows and aggregates to push alerts to VForce360.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The formatted message body to send.
     */
    void postMessage(String channel, String body);
}

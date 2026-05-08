package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the VForce360 workflow to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The formatted message body.
     */
    void postMessage(String channel, String body);
}

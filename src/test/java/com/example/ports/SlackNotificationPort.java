package com.example.ports;

import java.util.Map;

/**
 * Port for sending Slack notifications.
 * Used by the VForce360 defect reporting workflow.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body The formatted message body.
     * @return true if the API accepts the request, false otherwise.
     */
    boolean postMessage(String channel, String body);
}

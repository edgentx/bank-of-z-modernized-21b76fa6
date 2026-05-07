package com.example.ports;

import java.util.Map;

/**
 * Port for sending notifications to Slack.
 * Used by the temporal-worker logic to report defects.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param body The message body content
     * @return true if successful, false otherwise
     */
    boolean postMessage(String channel, String body);

    /**
     * Posts a message with structured blocks (not used in this specific story but good for completeness).
     */
    default boolean postMessage(String channel, Map<String, Object> blocks) {
        return false; 
    }
}

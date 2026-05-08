package com.example.ports;

/**
 * Port for publishing messages to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackPublisher {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues")
     * @param body The formatted body of the message
     */
    void publish(String channel, String body);
}

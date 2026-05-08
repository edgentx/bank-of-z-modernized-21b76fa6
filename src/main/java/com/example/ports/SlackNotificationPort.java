package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by defect reporting workflows to notify engineers.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345")
     * @param messageBody The formatted body of the message
     * @throws IllegalArgumentException if channelId or messageBody is null/empty
     */
    void postMessage(String channelId, String messageBody);

    /**
     * Posts a message to the default VForce360 issues channel.
     *
     * @param messageBody The formatted body of the message
     */
    void postToDefaultChannel(String messageBody);
}
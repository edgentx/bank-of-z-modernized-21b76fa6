package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the VForce360 defect reporting workflow.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean postMessage(String channel, String messageBody);
}
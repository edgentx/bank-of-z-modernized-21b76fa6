package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a configured Slack channel.
     *
     * @param text The body of the message to send.
     * @return true if the message was accepted by the client, false otherwise.
     * @throws IllegalArgumentException if text is null or empty.
     */
    boolean postMessage(String text);
}

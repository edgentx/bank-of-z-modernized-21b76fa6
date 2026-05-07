package com.example.ports;

/**
 * Port for Slack notification operations.
 * Used to decouple the domain logic from the actual Slack API implementation.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to the configured Slack channel.
     * @param body The message body content.
     * @return true if the message was accepted by the client.
     */
    boolean postMessage(String body);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used in the VForce360 validation workflow.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a configured Slack channel.
     *
     * @param messageBody The content of the message.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean postMessage(String messageBody);
}
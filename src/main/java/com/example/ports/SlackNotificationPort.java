package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by domain services to decouple from the specific Slack implementation.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message. Expected to contain GitHub links.
     * @return true if the notification was accepted by the client, false otherwise.
     */
    boolean sendMessage(String channel, String messageBody);
}
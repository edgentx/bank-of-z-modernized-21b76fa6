package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the domain logic to decouple from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message
     * @return The timestamp of the posted message, or null if failed.
     */
    String sendMessage(String channel, String messageBody);
}
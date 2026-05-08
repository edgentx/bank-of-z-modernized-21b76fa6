package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used in VForce360 integration workflows.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The content of the message.
     * @return The timestamp of the posted message, or null if sending failed.
     */
    String sendMessage(String channel, String messageBody);
}
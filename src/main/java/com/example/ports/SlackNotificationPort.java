package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the defect reporting logic from the actual Slack implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345")
     * @param messageBody The body of the message to send.
     * @return true if the message was accepted, false otherwise.
     */
    boolean postMessage(String channelId, String messageBody);

}
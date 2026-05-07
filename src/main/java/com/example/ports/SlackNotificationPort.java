package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by domain logic to decouple from the actual Slack WebAPI client.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C0123456789").
     * @param messageBody The content of the message.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean postMessage(String channelId, String messageBody);
}

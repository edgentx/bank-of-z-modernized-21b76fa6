package com.example.ports;

/**
 * Port interface for Slack notification services.
 * Used by the Activity implementation to decouple from specific HTTP clients.
 */
public interface SlackPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The ID or name of the channel.
     * @param message   The text content of the message.
     * @return true if the message was accepted by the Slack API.
     */
    boolean sendMessage(String channelId, String message);
}
package com.example.ports;

/**
 * Port interface for sending notifications (e.g., Slack).
 * Implementations will handle the actual HTTP/Websocket interactions.
 */
public interface NotificationPort {

    /**
     * Sends a message to a specific channel.
     *
     * @param channelId The target channel ID or name.
     * @param messageBody The content of the message.
     * @return true if sending was considered successful, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}

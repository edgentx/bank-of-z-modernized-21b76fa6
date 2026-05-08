package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {
    
    /**
     * Sends a message to a specific channel.
     * @param channel The channel ID or name.
     * @param messageBody The text content to send.
     * @return true if the message was accepted by the client.
     */
    boolean sendMessage(String channel, String messageBody);
}
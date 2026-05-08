package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackPort {

    /**
     * Posts a message to a specific Slack channel.
     * @param channel The channel ID or name.
     * @param messageBody The formatted content of the message.
     * @return true if the message was accepted by the API, false otherwise.
     */
    boolean postMessage(String channel, String messageBody);
}
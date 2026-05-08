package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Temporal workflow logic to communicate with the outside world.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The content of the message
     */
    void sendMessage(String channel, String messageBody);
}

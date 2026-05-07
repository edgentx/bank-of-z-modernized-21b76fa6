package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by Temporal workflows to decouple from the actual Slack SDK.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a channel.
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted message body
     * @return true if sending was successful
     */
    boolean postMessage(String channel, String messageBody);
}

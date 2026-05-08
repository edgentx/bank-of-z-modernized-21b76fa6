package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Validation workflow to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The formatted body of the message
     */
    void postMessage(String channel, String messageBody);
}

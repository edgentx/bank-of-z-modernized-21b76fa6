package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Temporal workflow worker to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param messageBody The formatted body of the message (supports Slack markdown).
     */
    void sendMessage(String messageBody);
}

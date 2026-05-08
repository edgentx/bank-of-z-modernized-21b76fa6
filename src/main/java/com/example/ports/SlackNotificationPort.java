package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the temporal worker to report defect information.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name.
     * @param messageBody The formatted body of the message.
     * @throws IllegalArgumentException if channel or body is invalid/empty.
     */
    void postMessage(String channel, String messageBody);
}

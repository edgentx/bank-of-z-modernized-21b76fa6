package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to report defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g. "#vforce360-issues").
     * @param messageBody The formatted body of the message.
     */
    void postMessage(String channel, String messageBody);
}

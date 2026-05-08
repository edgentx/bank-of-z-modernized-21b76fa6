package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to alert users of defects.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted message body
     */
    void sendMessage(String channelId, String messageBody);
}

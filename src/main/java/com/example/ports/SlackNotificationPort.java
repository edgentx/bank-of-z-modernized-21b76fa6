package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by domain workflows to report defects or status updates.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345")
     * @param messageBody The formatted content of the message (can include Slack mrkdwn)
     */
    void postMessage(String channelId, String messageBody);
}

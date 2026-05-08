package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to verify that defect report workflows generate correct URLs.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channelId The target channel ID (e.g., "C12345")
     * @param messageBody The formatted message body content.
     */
    void postMessage(String channelId, String messageBody);
}

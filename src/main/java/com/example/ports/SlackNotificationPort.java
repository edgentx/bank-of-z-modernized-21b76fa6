package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used in the defect reporting workflow to notify the channel.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The target channel ID.
     * @param messageBody The formatted message body.
     */
    void sendMessage(String channelId, String messageBody);
}

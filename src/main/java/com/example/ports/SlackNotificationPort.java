package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message body to a specific Slack channel.
     *
     * @param channelId The target channel ID.
     * @param messageBody The content of the message.
     */
    void sendMessage(String channelId, String messageBody);
}

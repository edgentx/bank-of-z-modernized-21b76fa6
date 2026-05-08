package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackPort {

    /**
     * Sends a message to a specific Slack channel.
     *
     * @param channelId The target channel ID or name.
     * @param messageBody The formatted message body.
     */
    void sendMessage(String channelId, String messageBody);
}

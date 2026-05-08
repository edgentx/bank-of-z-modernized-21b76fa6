package com.example.ports;

import java.util.Map;

/**
 * Port for sending notifications to Slack.
 * Used by Defect Reporting workflows to post status updates.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channelId The target channel ID.
     * @param messageBody The structured content of the message.
     */
    void sendMessage(String channelId, Map<String, Object> messageBody);
}

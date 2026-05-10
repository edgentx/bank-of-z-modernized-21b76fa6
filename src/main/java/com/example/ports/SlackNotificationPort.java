package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 * Used by the Temporal workflow implementation to post defect reports.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param body The text body of the message.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendMessage(String channel, String body);

    /**
     * Sends a rich message to a Slack channel.
     *
     * @param channel The Slack channel ID or name.
     * @param payload A map representing the JSON payload (blocks, attachments, etc.).
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendRichMessage(String channel, Map<String, Object> payload);
}

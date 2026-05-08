package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a specific channel.
     *
     * @param channel The channel ID or name.
     * @param body    The formatted message body.
     * @return true if sending was successful, false otherwise.
     */
    boolean sendMessage(String channel, String body);

    /**
     * Posts a rich message with metadata.
     */
    default boolean sendRichMessage(String channel, String text, Map<String, Object> metadata) {
        return false;
    }
}
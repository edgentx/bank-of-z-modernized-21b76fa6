package com.example.ports;

import java.util.List;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackPort {

    record MessageField(String title, String value) {}

    /**
     * Sends a message to a specific channel.
     * @param channel The channel ID or name
     * @param text The main text body
     * @param fields Optional structured fields
     */
    void sendMessage(String channel, String text, List<MessageField> fields);
}
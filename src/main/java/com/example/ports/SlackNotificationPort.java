package com.example.ports;

import java.util.Set;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a specific channel.
     * @param channel The target channel (e.g. "#vforce360-issues").
     * @param message The message body.
     * @param mentions Set of user IDs to mention (optional).
     */
    void sendMessage(String channel, String message, Set<String> mentions);
}
package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues").
     * @param body The text content of the message.
     */
    void postMessage(String channel, String body);

    /**
     * Retrieves the last posted message body for a specific channel.
     * Helper method primarily for verification in mock implementations or test harnesses.
     *
     * @param channel The target channel.
     * @return The body of the last message sent to this channel.
     */
    String getLastMessageBody(String channel);
}

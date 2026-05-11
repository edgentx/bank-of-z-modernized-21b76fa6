package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotifierPort {

    /**
     * Sends a notification message to a specific channel.
     *
     * @param channelId The target Slack channel ID or name.
     * @param message   The content of the message.
     * @return true if the message was successfully sent, false otherwise.
     */
    boolean notify(String channelId, String message);
}

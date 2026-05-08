package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to ensure the workflow properly formats the body with the GitHub URL.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channelId The target channel ID (e.g., "#vforce360-issues").
     * @param body      The formatted message body.
     */
    void postMessage(String channelId, String body);
}

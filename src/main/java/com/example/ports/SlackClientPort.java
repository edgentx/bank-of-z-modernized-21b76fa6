package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackClientPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The channel ID or name (e.g., "#vforce360-issues").
     * @param body The formatted body of the message.
     */
    void postMessage(String channel, String body);
}

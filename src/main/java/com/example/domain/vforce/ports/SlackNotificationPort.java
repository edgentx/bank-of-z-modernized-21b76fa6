package com.example.domain.vforce.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a channel.
     * @param channel The channel ID or name.
     * @param text The message body.
     */
    void postMessage(String channel, String text);
}
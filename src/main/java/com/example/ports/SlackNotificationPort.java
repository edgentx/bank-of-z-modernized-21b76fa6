package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to the configured Slack channel.
     *
     * @param text The body of the message to be posted.
     */
    void postMessage(String text);
}

package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by domain services to report defects.
 */
public interface SlackPort {
    /**
     * Posts a message to a configured Slack channel.
     *
     * @param text The body of the message to send.
     */
    void sendMessage(String text);
}

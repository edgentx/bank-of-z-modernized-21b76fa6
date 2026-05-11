package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackPort {
    /**
     * Sends a message to a Slack channel.
     * @param text The message text/markdown.
     */
    void sendMessage(String text);
}
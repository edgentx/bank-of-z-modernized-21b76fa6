package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackPort {

    /**
     * Sends a notification message to Slack.
     *
     * @param message The message payload to send.
     */
    void sendMessage(String message);
}

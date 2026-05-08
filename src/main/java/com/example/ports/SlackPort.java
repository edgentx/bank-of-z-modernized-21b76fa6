package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used to avoid dependencies on the real Slack client in unit tests.
 */
public interface SlackPort {

    /**
     * Sends a notification message to a configured Slack channel.
     *
     * @param message The body of the message to send.
     */
    void sendMessage(String message);
}
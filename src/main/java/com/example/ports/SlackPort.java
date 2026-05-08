package com.example.ports;

public interface SlackPort {
    /**
     * Sends a notification to a Slack channel.
     * @param message The message body (supports Slack formatting)
     */
    void sendMessage(String message);
}

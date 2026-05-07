package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * This abstraction allows us to mock the Slack client in tests.
 */
public interface SlackPort {
    
    /**
     * Sends a message to the configured Slack channel.
     *
     * @param message The message body to send.
     */
    void sendMessage(String message);
}

package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used to decouple the application logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to the configured Slack channel.
     *
     * @param messageBody The content of the message to be sent.
     * @throws IllegalArgumentException if the messageBody is null or empty.
     */
    void postMessage(String messageBody);
}

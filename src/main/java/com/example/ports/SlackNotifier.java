package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Implementations must handle the actual HTTP call to Slack Web API.
 */
public interface SlackNotifier {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param messageBody The content of the message to send.
     * @throws IllegalArgumentException if messageBody is null or empty.
     */
    void send(String messageBody);
}

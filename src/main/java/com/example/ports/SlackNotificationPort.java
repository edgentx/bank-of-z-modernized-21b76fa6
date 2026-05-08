package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack API implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param messageBody The content of the message to be sent.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean postMessage(String messageBody);
}
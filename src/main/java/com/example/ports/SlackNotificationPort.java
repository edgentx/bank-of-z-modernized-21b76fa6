package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used for defect reporting alerts.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param messageBody The content of the message to send.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendMessage(String messageBody);

    /**
     * Retrieves the last message body sent to the mock for verification in tests.
     * (This method is primarily for the Mock/Memory adapter interface).
     */
    default String getLastMessageBody() {
        throw new UnsupportedOperationException("Not implemented in production adapter");
    }
}

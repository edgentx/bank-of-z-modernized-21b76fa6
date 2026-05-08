package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The formatted message body to send.
     * @return true if the message was accepted by the client, false otherwise.
     */
    boolean sendNotification(String messageBody);
}

package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The content of the message to be sent.
     */
    void sendNotification(String messageBody);
}

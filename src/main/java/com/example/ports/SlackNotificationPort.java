package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Decouples the domain logic from the specific implementation of Slack messaging.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to the configured Slack channel/webhook.
     *
     * @param messageBody The formatted message body to send.
     */
    void send(String messageBody);
}

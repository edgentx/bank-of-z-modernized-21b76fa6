package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Allows domain logic to trigger alerts without depending on a concrete implementation.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The content of the message.
     * @return true if the notification was accepted, false otherwise.
     */
    boolean send(String messageBody);
}

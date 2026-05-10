package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Following the Adapter/Port pattern, this interface decouples the domain
 * from the specific implementation of the Slack client.
 */
public interface SlackNotificationPort {
    /**
     * Sends a notification message to the configured Slack channel.
     * @param messageBody The formatted message body.
     */
    void sendNotification(String messageBody);
}

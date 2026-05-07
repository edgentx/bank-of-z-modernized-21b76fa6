package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used in end-to-end defect reporting workflows.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message payload to a configured Slack channel.
     *
     * @param messageBody The formatted JSON string to be sent to Slack.
     * @throws RuntimeException if the notification fails to send.
     */
    void sendNotification(String messageBody);
}

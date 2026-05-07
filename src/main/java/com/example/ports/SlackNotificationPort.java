package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the defect reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param messageBody The body of the message to send. Expected to contain the GitHub URL.
     */
    void sendNotification(String messageBody);
}

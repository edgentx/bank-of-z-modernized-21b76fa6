package com.example.ports;

/**
 * Interface for sending Slack notifications.
 * Port definition.
 */
public interface SlackNotifier {
    /**
     * Sends a notification message to Slack.
     */
    void sendNotification(String body);
}

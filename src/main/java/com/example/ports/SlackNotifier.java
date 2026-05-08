package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Defect Reporting workflow.
 */
public interface SlackNotifier {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The formatted JSON payload intended for the Slack Incoming Webhook.
     * @throws RuntimeException if the notification fails (e.g., network error, 4xx response).
     */
    void sendNotification(String payload);
}
